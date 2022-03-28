package cz.wake.sussi.utils;

import java.util.List;

public class MemberUtils {

    public static String idListToMention(List<String> memberIds) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String id : memberIds){
            stringBuilder.append("<@" + id + "> ");
        }
        return stringBuilder.toString();
    }

    public static List<String> addMemberToIdList(List<String> memberIds, String userId) {
        if(!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
        return memberIds;
    }

    public static List<String> removeMemberFromIdList(List<String> memberIds, String userId) {
        memberIds.remove(userId);
        return memberIds;
    }
}
