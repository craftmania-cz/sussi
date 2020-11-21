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
        this.cache().thenAcceptAsync(x -> this.check());
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
            // Debug
            SussiLogger.infoMessage("Checking user: " + member.getUser().getAsTag() + ".");
            // Member's vip roles
            final Set<Role> vipRoles = member.getRoles().stream().filter(role -> isVIPRole(role.getName())).collect(Collectors.toSet());
            final Optional<Profile> optionalProfile = getProfileByID(member.getId());

            for (Role vipRole : vipRoles) {
                SussiLogger.debugMessage("1");
                // Check if user should have this role
                if (!optionalProfile.isPresent()) {
                    // Does not have any VIP in his profile - all expired
                    this.removeVIPRole(guild, member, vipRole, "Expired VIP");
                    continue;
                }

                final Profile profile = optionalProfile.get();

                final Profile.VIPType vipType = Profile.VIPType.getFromRoleName(vipRole.getName());
                final HashMap<Profile.VIPType, Profile.ServerVIP> bestVIPsFromEachLevel = profile.getBestVIPsFromEachLevel();

                SussiLogger.debugMessage("2");

                if (vipType == null) {
                    // Probably incorrect VIP role?
                    this.removeVIPRole(guild, member, vipRole, "Invalid role");
                    continue;
                }

                SussiLogger.debugMessage("3");

                if (!bestVIPsFromEachLevel.containsKey(vipType)) {
                    // Does have this VIP type in
                    this.removeVIPRole(guild, member, vipRole, "Expired VIP");
                    continue;
                }
            }

            if (!optionalProfile.isPresent()) continue;

            final Profile profile = optionalProfile.get();
            final HashMap<Profile.VIPType, Profile.ServerVIP> bestVIPsFromEachLevel = profile.getBestVIPsFromEachLevel();

            SussiLogger.debugMessage("4");

            for (Map.Entry<Profile.VIPType, Profile.ServerVIP> vipTypeServerVIPEntry : bestVIPsFromEachLevel.entrySet()) {
                if (member.getRoles().stream().noneMatch(role -> role.getName().equals(vipTypeServerVIPEntry.getKey().getDiscordRoleName()))) {
                    if (vipTypeServerVIPEntry.getValue() == null) continue;
                    // Has this VIP in profile but not as a role
                    SussiLogger.debugMessage("5");
                    this.addVIPRole(guild, member, guild.getRolesByName(vipTypeServerVIPEntry.getKey().getDiscordRoleName(), false).get(0), vipTypeServerVIPEntry.getValue());
                    continue;
                }
            }

            /*if (vipRoles.isEmpty()) {
                // Does not have any roles assigned, check if we can assign some roles
                if (!optionalProfile.isPresent()) continue; // Could not find profile by his ID - does not have any VIPs

                final Profile profile = optionalProfile.get();
                final HashMap<Profile.VIPType, Profile.ServerVIP> bestVIPsFromEachLevel = profile.getBestVIPsFromEachLevel();

                for (Role vipRole : vipRoles) {
                    // Check if player should have this VIP role
                    Profile.VIPType vipType = Profile.VIPType.getFromRoleName(vipRole.getName());

                    if (vipType == null) {
                        // Probably incorrect VIP role?
                        this.removeVIPRole(guild, member, vipRole, "Invalid role");
                        continue;
                    }

                    // Correct VIP role - check if he has this VIP type in profile

                    if (bestVIPsFromEachLevel.containsKey(vipType)) {
                        // Does have this VIP type in
                        this.removeVIPRole(guild, member, vipRole, "Expired VIP");
                    }
                }
            } else {
                // Does have some VIPs
            }*/

            /*if (vipRoles.isEmpty()) {
                if (!optionalProfile.isPresent()) continue;

                Profile profile = optionalProfile.get();
                if (!profile.hasGlobalVIP()) continue;
                // Has expired Global VIP (it did not refresh lol)
                if (profile.getGlobalVIP().getTime() < System.currentTimeMillis()) continue;

                //SussiLogger.debugMessage("1");
                // Does not have any role, but has global VIP
                if (member.getRoles().stream().anyMatch(role -> role == guild.getRolesByName(profile.getGlobalVIP().getDiscordRoleName(), true).get(0))) {
                    SussiLogger.infoMessage("User " + member.getUser().getAsTag() + " already has activated role " + profile.getGlobalVIP().getDiscordRoleName() + ".");
                    continue;
                }
                guild.addRoleToMember(member, guild.getRolesByName(profile.getGlobalVIP().getDiscordRoleName(), true).get(0)).queue();
                SussiLogger.infoMessage("Activated " + profile.getGlobalVIP().getDiscordRoleName() + " role to user " + member.getUser().getAsTag() + ".");
                member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                        MessageUtils.getEmbed(Constants.BLUE)
                        .setDescription("Byla ti nastavena role `" + profile.getGlobalVIP().getDiscordRoleName() + "`." +
                                (profile.getGlobalVIP().isPermanent() ? "" : "\nExpirace: " + profile.getGlobalVIP().getFormattedDate()))
                        .build()
                ).queue());
            } else {
                //SussiLogger.debugMessage("2");
                // Player has global VIP and roles already
                if (optionalProfile.isPresent()) continue;

                // Does have role, but does not have global VIP - thus remove this role
                for (Role vipRole : vipRoles) {
                    guild.removeRoleFromMember(member, vipRole).queue();
                }
                SussiLogger.infoMessage("Removing VIP roles from user " + member.getUser().getAsTag() + ".");
                member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(
                        MessageUtils.getEmbed(Constants.RED)
                                .setTitle("Byla ti odebrána VIP role")
                                .setDescription("Důvody proč se tohle mohlo stát:\n" +
                                        "1. Tvé Global VIP expirovalo.\n" +
                                        "2. Nemáš linknutý Discord účet s MC účtem - napiš `,link` v <#207805056123273216>.\n" +
                                        "3. Nastala chyba v žabičkovém systému, napiš Wakovi.")
                                .build()
                ).queue());
            }*/
        }
        long then = System.currentTimeMillis();
        long diff = then - now;
        SussiLogger.greatMessage("VIP checking finished (" + diff + "ms).");
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
                                "2. Nemáš linknutý Discord účet s MC účtem - napiš `,link` v <#207805056123273216>.\n" +
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
                        .setDescription((serverVIP.isPermanent() ? "" : "\nExpirace: " + serverVIP.getFormattedDate()))
                        .build()
        ).queue());

        SussiLogger.infoMessage("Activated " + role.getName() + " role to user " + member.getUser().getAsTag() + ".");
    }
}
