package FriendCrash;


import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Timer;


public class Friend extends Thread {

    //NOTE: this code

    Character[] plans = new Character[4];
    char[][] reportedPlans = new char[4][4];
    char[] majorityPlan = new char[4];
    int id;
    Helper helper;
    Random random = new Random();
    BlockingQueue<Helper.Plan> recievedQueue = new ArrayBlockingQueue(3);
    BlockingQueue<Helper.Package> reportedQueue = new ArrayBlockingQueue(8);
    ArrayList<Helper.Package> numReplies = new ArrayList<>();


    Friend(int id, Helper s) {
        this.id = id;
        helper = s;
    }

    public void run() {

        //each friend randomly picks a plan
        char p = random.nextBoolean() ? 'i' : 'o'; //randomly pick indoors or outdoors
        plans[id] = p;

        //first round - send all other friends the plan
        if (this.id == 0) {
            for (int i = 0; i < 4; i++) {
                if (i == id) {
                    continue;
                }
                try {
                    helper.send(i, id, random.nextBoolean() ? '-' : plans[id]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (i == id) {
                    continue;
                }
                try {
                    helper.send(i, id, plans[id]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            receive(); //receive replies from the other nodes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //for DEBUGGING purposes
        System.out.println(currentThread().getName() + ": round 1 plans " + Arrays.toString(plans));


        //second round - send each friend the plans received from the other friends

        //if it's the forgetful friend sending the messages, some of the message sent
        // should be '-' indicating s/he has temporarily 'forgot'/crashed
        if (id == 0) {
            for (int F1 = 0; F1 < 4; F1++) {
                //skip sending the plans to themself
                if (F1 == id) {continue;}

                for (int F2 = 0; F2 < 4; F2++) {
                    //skip itself or sending the friend's own plan to himself/herself
                    if (F2 == id | F1 == F2) { continue;}

                    try {
                        //send either their plan or nothing denoted by "-"
                        helper.secondSend(F2, id, F1, random.nextBoolean() ? '-' : plans[id]);

                    } catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        }
        //rest of the friends should honestly pass their reported plans
        else {
            for (int F1 = 0; F1 < 4; F1++) {
                //skip sending the plans to itself
                if (F1 == id) {continue;}

                for (int F2 = 0; F2 < 4; F2++) {
                    //skip itself or sending the friend's own plan to himself/herself
                    if (F2 == id || F1 == F2) { continue;}

                    try {
                        //send the reported plans send(G', myID, G, plan[G])
                        helper.secondSend(F2, id, F1, plans[F1]);
                        // for DEBUGGING purposes
//                        System.out.println(F2 + " received: according to "+ id+ " " + F1 +"'s plan is "+plans[F1]+"  " + currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            receiveReported();// receive the reported plans
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //for DEBUGGING purpose - prints the 2-d array
        System.out.println(currentThread().getName()+ ": round 2 plans " +Arrays.deepToString(reportedPlans));

        sleep(400);//to separate the print-statement blocks

        //First voting
        for (int i = 0; i < 4; i++) {
            //count the occurrences of i and o in the arrays
            int indoorNum = Majority('i', plans[i], reportedPlans[i]);//majority(plan[G] ∪ reportedPlan[*, G]
            int outdoorNum = Majority('o', plans[i], reportedPlans[i]);//majority(plan[G] ∪ reportedPlan[*, G]
            if(indoorNum==0 && outdoorNum==0){
                majorityPlan[i] = '-';// if faulty friend crashes in every msg
            }
            else if (indoorNum >= outdoorNum) {
                majorityPlan[i] = 'i';
            } else {
                majorityPlan[i] = 'o';
            }
        }

        //Second voting
        int indoorNum = secondMajority('i', majorityPlan);
        int outdoorNum = secondMajority('o', majorityPlan);

        if (indoorNum >= outdoorNum) {
            System.out.println(Thread.currentThread().getName() + " has decided to stay Indoors");
        } else {
            System.out.println(Thread.currentThread().getName() + " has decided is to stay Outdoors");
        }

    }


    //                            Helper Functions
//   *******************************************************************
    private void receive() throws InterruptedException {

        while (true) {
            Helper.Plan receivedPlan = recievedQueue.take(); //receive(request, source, requestedNum)
            plans[receivedPlan.id] = receivedPlan.plan;
            if (!Arrays.asList(plans).contains(null)) {
                break;//once all the arrays are full, break
            }
        }
    }

    public void receiveReported() throws InterruptedException {

        while (true) {
            Helper.Package reportPlan = reportedQueue.take(); //receive(G, G’, reportedPlan[G, G’])
            reportedPlans[reportPlan.x][reportPlan.y] = reportPlan.plan;
            numReplies.add(reportPlan);
            if (numReplies.size() == 6) {//once all 6 message have been received, break
                break;
            }
        }
    }

    // function to count the occurrence of selected char in array
    int Majority(char friendPlan, char ownPlan, char[] list2) {

        int occr = 0;
        for (int i = 0; i < list2.length; i++) {
            if (list2[i] == friendPlan) {
                occr++;
            }
        }
        //add also a node's own plan to the occurrence number
        if (ownPlan == friendPlan) {
            occr++;
        }
        return occr;
    }

    // function to count the occurrence of selected char in array
    int secondMajority(char c, char[] list2) {
        char plan = c;
        int occr = 0;

        for (int i = 0; i < list2.length; i++) {
            if (list2[i] == plan) {
                occr++;
            }
        }
        return occr;
    }

    //simple sleep function - just to tidy the code in the main section
    void sleep(int num) {
        try {
            currentThread().sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

    //FIXED!!!!

