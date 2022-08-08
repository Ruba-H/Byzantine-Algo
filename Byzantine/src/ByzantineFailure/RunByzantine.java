package ByzantineFailure;


public class RunByzantine {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("**********  Byzantine Failure! - Friend 0 will send wrong messages *******");

        Friend[] friends = new Friend[4];
        Helper helper = new Helper();

        // Initialize the friend threads.
        for (int i = 0; i < friends.length; i++) {

            friends[i] = friends[i] = new Friend(i, helper);
            friends[i].setName("Friend " + i);
            helper.mailbox.put(i,friends[i]); //add all the friend details in the mail system
        }

        //start the friend threads
        for (Friend f : friends) {
            f.start();
            f.sleep(200);
        }

    }
}
