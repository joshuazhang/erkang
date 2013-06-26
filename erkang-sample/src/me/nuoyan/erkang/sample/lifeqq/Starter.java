package me.nuoyan.erkang.sample.lifeqq;

import me.nuoyan.opensource.creeper.schedule.ListSchedule;
import me.nuoyan.opensource.creeper.schedule.Scheduler;

public class Starter {

    public static String DD_URL_PREFIX = "http://meishi.qq.com/";


    public static void main(String[] args) throws Exception {

        ListSchedule schedule = Scheduler.getSchedule(Starter.class.getResourceAsStream("/me/nuoyan/erkang/sample/lifeqq/schedule.xml"));
        Scheduler.doSchedule(schedule);
    }
}
