package com.gy.community.utils;

public class RedisKeyUtil {
    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWER = "follewer";

    private static final String PREFIX_FOLLOWEE = "follewee";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    private static final String PREFIX_UV = "uv";

    private static final String PREFIX_DAU = "dau";

    private static final String PREFIX_POST = "post";



    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //follewee:userId:entityType -> Zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个用户拥有的粉丝
    //follewer:entityType:entityId -> Zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登陆验证码
    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登陆凭证
    public static String getTicket(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER+ SPLIT + userId;
    }

    //单日UV
    public static String getUVKey(String date){
        return PREFIX_UV+ SPLIT + date;
    }

    //区间UV
    public static String getUVKey(String begin, String end){
        return PREFIX_UV + SPLIT + begin + SPLIT + end;
    }

    //单日DAU
    public static String getDAUKey(String date){
        return PREFIX_DAU+ SPLIT + date;
    }

    //区间DAU
    public static String getDAUKey(String begin, String end){
        return PREFIX_DAU + SPLIT + begin + SPLIT + end;
    }

    //缓存帖子，用来帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }

}
