package ch.rasc.twofa.util;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Date;

public class SmsSender {
    // Find your Account Sid and Auth Token at twilio.com/console
    // 8xtLJg8yBg-DYvNjEkjHi9-TJtxkQqszZlxxiO4W
    public static final String ACCOUNT_SID =
            "AC9a4c8ea24f4361f1f3d527cf921c2b84";
    public static final String AUTH_TOKEN =
            "5362725e5b5e6e5238e4d7d743f2dde4";

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        PhoneNumber senderNumber = new PhoneNumber("+14158818946");
        String[] numbers = {"+18572321062", "+18583717705", "+18436306420"};
        long start = System.currentTimeMillis();
        int i = 0;
        while (i < 3) {
            for (int j = 0; j < numbers.length; j++) {
                Message
                    .creator(new PhoneNumber(numbers[j]), // to
                            senderNumber, // from
                            "Magic sms test---- to " + numbers[j] + new Date())
    //                .setMediaUrl(
    //                        Promoter.listOfOne(URI.create("https://www.w3schools.com/w3css/img_lights.jpg"))
                    .create();
            }
            i++;
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start)/1000);//        printAllDeliveryStatus();
//        printStatusAsync();
    }

    private static void printAllDeliveryStatus() {
        ResourceSet<Message> messages = Message.reader().read();
        for (Message message : messages) {

            System.out.println(message.getSid() + " : " + message.getStatus()
                    + " -- " + message.getDateSent());
        }
    }

    /* unit test */
    /* error handling */
    /* if unsuccessful, */
    private static void printStatusAsync() {
        ListenableFuture<ResourceSet<Message>> future = Message.reader().readAsync();
        Futures.addCallback(
                future,
                new FutureCallback<ResourceSet<Message>>() {
                    public void onSuccess(ResourceSet<Message> messages) {
                        for (Message message : messages) {
                            System.out.println(message.getSid() + " : " + message.getStatus());
                        }
                    }
                    public void onFailure(Throwable t) {
                        System.out.println("Failed to get message status: " + t.getMessage());
                    }
                });
    }

}
