package NoFailure;

import java.util.HashMap;

public class Helper {
    // serves as the mailbox, connecting the ids with the respective friend objects
    HashMap<Integer, Friend> mailbox = new HashMap<Integer, Friend>();

    class Plan{
        int id;
        char plan;

        Plan(int id, char plan){
            this.id = id;
            this.plan = plan;
        }
    }

    class Package{
        char plan;
        int x;
        int y;

        Package(char plan, int x, int y){
            this.plan = plan;
            this.x = x;
            this.y = y;
        }
    }


    void send(Integer receivingFriend , Integer sourceFriend, Character plan) throws InterruptedException {
        Plan recievedPlan = new Plan(sourceFriend, plan);
        mailbox.get(receivingFriend).recievedQueue.put(recievedPlan);
    }

    void secondSend(Integer receivingFriend, Integer originID, Integer sourceFriend, Character plan) throws InterruptedException {
        Package reportedPlans = new Package(plan, sourceFriend, originID);
        mailbox.get(receivingFriend).reportedQueue.put(reportedPlans);
    }


}
