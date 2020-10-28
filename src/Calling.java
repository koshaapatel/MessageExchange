import java.util.Random;

public class Calling extends Thread {
    Exchange master = new Exchange();
    public Calling(String threadName){
        super(threadName);
    }


    public void introMsg(Calling sender){
        Random rnd = new Random();
        int currentTime = rnd.nextInt(999989)+1;

        master.printIntroInMaster(this.getName() + " got intro message from " + sender.getName(), currentTime);
        try {
            sender.sleep(new Random().nextInt(100) + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sender.sendReply(this, currentTime);
    }

    public void sendReply(Calling sender, int time){
        master.printReplyInMaster(this.getName() + " got reply message from " + sender.getName(), time);
    }


    @Override
    public void run() {
        synchronized (this) {
            try {
                sleep(new Random().nextInt(100) + 1);
            } catch (InterruptedException e) { }

            String[] receivers = master.userHM.get(getName()).split(",");
            for (String receiver : receivers) {
                master.threadHM.get(receiver).introMsg(this);
            }

            try {
                terminateThread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminateThread() throws InterruptedException {
        if(master.threadFinishedCounter < master.threadHM.size()) {
            master.threadFinishedCounter ++;
            synchronized (master.threadHM) {
                try {
                    master.threadHM.wait();
                    Random rand = new Random();
                    sleep(rand.nextInt(500));
                    System.out.println("\n**Process " + getName() + " has received no calls for 5 second, ending...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            synchronized (master.threadHM){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\nProcess " + getName() + " has received no calls for 5 second, ending...");
                master.threadHM.notifyAll();
            }
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            master.getNotifyInMaster();
        }
    }
}
