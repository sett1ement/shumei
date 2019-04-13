import java.util.Random;
//由于我理解不够到位，因此借鉴了https://blog.csdn.net/qq_27717967/article/details/78388450的内容
//在大致了解了原理的基础上，基本是每写完一部分，再与网上内容进行对照
public class DivideRedpackage {
    public static void main(String[] args) {
        //100元钱，16个红包
        redPackage redPackage = new redPackage(100, 16);
        User user = new User( redPackage);
        for( int i = 0; i < 20; i++) {
            //一个用户一个线程。没搞懂为什么运行结果Thread-X不是按顺序排的
            new Thread(user).start();
        }
    }
}

class redPackage{
    private int totalVal;
    private int count;

    redPackage(double total, int count) {
        this.count = count;
        //由于小数不精确，因此*100进行操作。或者使用BigDecimal
        this.totalVal = (int)( total * 100);
    }

    synchronized double getRandomMoney() {
        int val;
        //考虑每人都只能分到一分的情况
        if(count != 0 && totalVal / count == 1) {
            val = 1;
            totalVal = totalVal - val;
            count--;
            return val/100.0;
        }

        if( count <= 0) {
            val = 0;
        }else if( count == 1) {
            val = totalVal;
        }else {
            //temp：现有的钱
            int temp;
            do {
                //尽可能平均分配且除了随机到0外都大于等于1
                val = new Random().nextInt(totalVal / count);
                temp = totalVal - val;
                //System.out.println("生成金额：" + val/100.0 + "剩余金额" + temp/100.0 + "剩余人数" + (count - 1));
                //在剩下的人都至少可以分到一分或着产生的钱比零大时继续
            } while (!(temp * 1.0 / (count - 1) >= 1) || val == 0);
            totalVal = totalVal - val;
        }
        count--;
        return val/100.0;
    }
}

class User implements Runnable{
    private redPackage redPackage;
    User(redPackage redPackage) {
        this.redPackage = redPackage;
    }
    @Override
    public void run() {
        double money = redPackage.getRandomMoney();
        if( money == 0) {
            System.out.println(Thread.currentThread().getName() + "钱被抢光了！" + " 你的优先级为" + Thread.currentThread().getPriority() + " ID" + Thread.currentThread().getId());
        }else {
            System.out.println(Thread.currentThread().getName() + "抢了" + money + "元" + " 你的优先级为" + Thread.currentThread().getPriority() + " ID" + Thread.currentThread().getId());
        }
    }
}
