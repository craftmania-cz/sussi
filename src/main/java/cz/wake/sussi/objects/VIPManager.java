package cz.wake.sussi.objects;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VIPManager {

    private Set<Profile> vipProfiles;

    public VIPManager() {
        this.vipProfiles = new HashSet<>();
        //this.cache().thenAcceptAsync(x -> this.check());
    }

    /**
     * Reloads cache (clears and loads again). Then checks for all available VIPs to give.
     */
    public void recheck() {
        this.cache().thenAcceptAsync(x -> this.check());
    }

    /**
     * Looks for all linked profiles in database. Then requests profiles
     * from API endpoint and checks if profile has any global VIP, if yes,
     * this profile is added to the cache.
     */
    private CompletableFuture<Set<Profile>> cache() {
        SussiLogger.infoMessage("Caching VIPs...");
        long now = System.currentTimeMillis();
        CompletableFuture<Set<Profile>> completableFuture = new CompletableFuture<>();
        try {
            Set<String> nicknames = Sussi.getInstance().getSql().getLinkedProfiles();
            vipProfiles.clear();
            for (String nickname : nicknames) {
                try {
                    Profile profile = new Profile(nickname);
                    if (profile.hasAnyVIP()) vipProfiles.add(profile);
                } catch (Exception e) {
                    SussiLogger.dangerMessage("Could not load profile for " + nickname + ".");
                    e.printStackTrace();
                }
            }
            long then = System.currentTimeMillis();
            long diff = then - now;
            SussiLogger.greatMessage("VIPs cached (" + vipProfiles.size() + ") [" + diff + "ms].");
            completableFuture.complete(vipProfiles);
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
            e.printStackTrace();
        }
        return completableFuture;
    }

    private void check() {
        long now = System.currentTimeMillis();
        SussiLogger.infoMessage("Checking for VIPs...");
        final Guild guild = Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID());
        if (guild == null) return;
        for (Member member : guild.getMembers().stream().filter(member -> !member.getUser().isBot()).collect(Collectors.toSet())) {
            this.checkMember(guild, member);
        }
        long then = System.currentTimeMillis();
        long diff = then - now;
        SussiLogger.greatMessage("VIP checking finished (" + diff + "ms).");
    }

    public void checkMember(Guild guild, Member member) {
        // Debug
        //SussiLogger.infoMessage("Checking user: " + member.getUser().getAsTag() + ".");
        // Member's vip roles
        final Set<Role> vipRoles = member.getRoles().stream().filter(role -> isVIPRole(role.getName())).collect(Collectors.toSet());
        final Optional<Profile> optionalProfile = getProfileByID(member.getId());

        //SussiLogger.debugMessage("0");

        for (Role vipRole : vipRoles) {
            //SussiLogger.debugMessage("1");
            // Check if user should have this role
            if (!optionalProfile.isPresent()) {
                // Does not have any VIP in his profile - all expired
                this.removeVIPRole(guild, member, vipRole, "Expired VIP");
                continue;
            }

            final Profile profile = optionalProfile.get();

            if (profile.getStatusId() == 429 || profile.getStatusId() == 500) {
                // Too many requests
                continue;
            }

            final Profile.VIPType vipType = Profile.VIPType.getFromRoleName(vipRole.getName());
            final HashMap<Profile.VIPType, Profile.ServerVIP> bestVIPsFromEachLevel = profile.getBestVIPsFromEachLevel();

            //SussiLogger.debugMessage("2");

            if (vipType == null) {
                // Probably incorrect VIP role?
                this.removeVIPRole(guild, member, vipRole, "Invalid role");
                continue;
            }

            //SussiLogger.debugMessage("3");

            if (!bestVIPsFromEachLevel.containsKey(vipType)) {
                // Does have this VIP type in
                this.removeVIPRole(guild, member, vipRole, "Expired VIP");
                continue;
            }
        }

        if (!optionalProfile.isPresent()) return;

        final Profile profile = optionalProfile.get();
        final HashMap<Profile.VIPType, Profile.ServerVIP> bestVIPsFromEachLevel = profile.getBestVIPsFromEachLevel();

        //SussiLogger.debugMessage("4");

        for (Map.Entry<Profile.VIPType, Profile.ServerVIP> vipTypeServerVIPEntry : bestVIPsFromEachLevel.entrySet()) {
            if (member.getRoles().stream().noneMatch(role -> role.getName().equals(vipTypeServerVIPEntry.getKey().getDiscordRoleName()))) {
                if (vipTypeServerVIPEntry.getValue() == null) continue;
                // Has this VIP in profile but not as a role
                //SussiLogger.debugMessage("5");
                this.addVIPRole(guild, member, guild.getRolesByName(vipTypeServerVIPEntry.getKey().getDiscordRoleName(), false).get(0), vipTypeServerVIPEntry.getValue());
                continue;
            }
        }
    }

    /**
     * @return All profiles with global VIP
     */
    public Set<Profile> getVIPProfiles() {
        return vipProfiles;
    }

    /**
     * @return Returns all profiles with permanent global VIP
     */
    public Set<Profile> getPermanentVIPs() {
        return vipProfiles.stream().filter(profile -> profile.getGlobalVIP().isPermanent()).collect(Collectors.toSet());
    }

    /**
     * @return Returns all profiles with temporary global VIP
     */
    public Set<Profile> getTemporaryVIPs() {
        return vipProfiles.stream().filter(profile -> !profile.getGlobalVIP().isPermanent()).collect(Collectors.toSet());
    }

    /**
     * @param ID User's ID
     * @return Returns all profiles with any VIP
     */
    public Optional<Profile> getProfileByID(String ID) {
        return this.vipProfiles.stream().filter(profile -> profile.getDiscordID().equalsIgnoreCase(ID)).findFirst();
    }

    /**
     * @param roleName Role name to check
     * @return True if role name is VIP role
     */
    private boolean isVIPRole(String roleName) {
        switch (roleName) {
            case "Obsidian VIP":
            case "Emerald VIP":
            case "Diamond VIP":
            case "Gold VIP":
                return true;
            default:
                return false;
        }
    }

    private void removeVIPRole(Guild guild, Member member, Role role, String reason) {
        guild.removeRoleFromMember(member, role).queue();
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                MessageUtils.getEmbed(Constants.RED)
                        .setTitle("Byla ti odebrána VIP role: " + role.getName() + "")
                        .setDescription("Důvody proč se tohle mohlo stát:\n" +
                                "1. Tvé VIP expirovalo.\n" +
                                "2. Nemáš linknutý Discord účet s MC účtem - více se dozvíš v <#731820662271901716>.\n" +
                                "3. Tenhle typ VIP již neexistuje.\n" +
                                "4. Nastala chyba v žabičkovém systému, napiš Wakovi.")
                        .build()
        ).queue());

        SussiLogger.infoMessage("Removed role " + role.getName() + " from user " + member.getUser().getAsTag() + " (reason: " + reason + ").");
    }

    private void addVIPRole(Guild guild, Member member, Role role, Profile.ServerVIP serverVIP) {
        guild.addRoleToMember(member, role).queue();
        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                MessageUtils.getEmbed(Constants.BLUE)
                        .setTitle("Byla ti nastavena VIP role: " + role.getName())
                        .setDescription((serverVIP.isPermanent() ? "Tato role neexpiruje." : "\nExpirace: " + serverVIP.getFormattedDate()))
                        .build()
        ).queue());

        SussiLogger.infoMessage("Activated " + role.getName() + " role to user " + member.getUser().getAsTag() + ".");
    }
}
