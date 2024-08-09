package org.example.calculator;

import org.example.kafka.KafkaOperator;
import org.example.requestMessage.AkkaRequestMessage;
import org.example.voltdb.VoltDbOperation;

import java.math.BigDecimal;

public class BalanceCalculations {

    VoltDbOperation voltOperation = new VoltDbOperation();
    KafkaOperator kafkaOperator = new KafkaOperator();


    public void calculateVoiceRequest(AkkaRequestMessage requestMessage){
        System.out.println("VOICE Request Calculating ...");

        int requestUsageAmount = requestMessage.getUsageAmount();
        double totalPrice = requestMessage.getTotalUsagePrice();
        long msisdn = Long.parseLong(requestMessage.getSenderMSISDN());


        var userVoiceBalance = voltOperation.getMinutesBalance(msisdn);
        var userWalletBalance = voltOperation.getMoneyBalance(msisdn);
        var uID = voltOperation.getUserID(msisdn);


        System.out.println("User ID: "+(int)uID);
        System.out.println("Request Amount: "+requestUsageAmount);
        System.out.println("Total Price: "+(int) totalPrice);
        System.out.println("Voice Balance: "+userVoiceBalance);
        System.out.println("Wallet Balance: "+userWalletBalance);

        if(userVoiceBalance <= 0){

            if(userWalletBalance <= 0){

                System.out.println("No Sufficient VOICE and WALLET Balance");

            } else if(userWalletBalance >= totalPrice){

                System.out.println("VOICE Request * WALLET * Condition");


                System.out.println("VOICE request Used ** WALLET ** Balance");
                voltOperation.updateMoneyBalance(msisdn, -((int) totalPrice));
                //kafkaOperator.sendKafkaWalletMessage(msisdn, (int) uID, (int)totalPrice);
                System.out.println("*** DB SENT ***");

            } else if(userWalletBalance < totalPrice){

                System.out.println("No Sufficient WALLET Balance");

            }
        } else if(userVoiceBalance >= requestUsageAmount){

            System.out.println("VOICE Request * NORMAL * Condition");


            System.out.println("VOICE request Used ** BALANCE **  Used");
            voltOperation.updateVoiceBalance(msisdn, requestUsageAmount);
            //kafkaOperator.sendKafkaUsageMessage(requestMessage.getType(), msisdn, (int) uID, requestMessage.getUsageAmount());
            System.out.println("*** DB SENT ***");

        } else if(userVoiceBalance < requestUsageAmount){

            System.out.println("VOICE Request * CALCULATED * Condition");


            requestMessage.setUsageAmount(requestUsageAmount - userVoiceBalance);
            requestMessage.calculateTotalPrice();

            System.out.println("Voice Request Used ** CALCULATED WALLET ** Balance");
            voltOperation.updateMoneyBalance(msisdn, -((int) requestMessage.getTotalUsagePrice()));
            //kafkaOperator.sendKafkaWalletMessage(msisdn, (int) uID, (int) requestMessage.getTotalUsagePrice());
            System.out.println("*** DB SENT ***");


            int remainingVoiceAmount =  userVoiceBalance;


            System.out.println("Voice Request Used ** CALCULATED VOICE ** Balance");
            voltOperation.updateVoiceBalance(msisdn, remainingVoiceAmount);
            //kafkaOperator.sendKafkaUsageMessage(requestMessage.getType(), msisdn, (int) uID, userVoiceBalance);
            System.out.println("*** DB SENT ***");
        }
    }

    public void calculateSMSRequest(AkkaRequestMessage requestMessage){
        System.out.println("SMS Request Calculating ...");

        int requestUsageAmount = requestMessage.getUsageAmount();
        double totalPrice = requestMessage.getTotalUsagePrice();
        long msisdn = Long.parseLong(requestMessage.getSenderMSISDN());

        var userSMSBalance = voltOperation.getSmsBalance(msisdn);
        var userWalletBalance = voltOperation.getMoneyBalance(msisdn);
        var uID = voltOperation.getUserID(msisdn);

        System.out.println("User ID: "+(int)uID);
        System.out.println("Request Amount: "+requestUsageAmount);
        System.out.println("Total Price: "+(int) totalPrice);
        System.out.println("SMS Balance: "+userSMSBalance);
        System.out.println("Wallet Balance: "+userWalletBalance);

        if(userSMSBalance <= 0){
            if(userWalletBalance <= 0){

                System.out.println("No Sufficient WALLET and SMS Balance");

            } else if (userWalletBalance >= totalPrice) {
                System.out.println("SMS Request * WALLET * Condition");

                System.out.println("SMS request Used ** WALLET ** Balance");
                voltOperation.updateMoneyBalance(msisdn, -((int) totalPrice));
                //kafkaOperator.sendKafkaWalletMessage(msisdn, (int) uID, (int)totalPrice);
                System.out.println("*** DB SENT ***");

            }
        } else {//user has sms balance
            System.out.println("SMS Request * NORMAL * Condition");

            System.out.println("SMS request Used ** SMS ** Balance");
            voltOperation.updateSmsBalance(msisdn, requestMessage.getUsageAmount());
            //kafkaOperator.sendKafkaUsageMessage(requestMessage.getType(), msisdn, (int) uID, requestUsageAmount);
            System.out.println("*** DB SENT ***");

        }

    }

    public void calculateDataRequest(AkkaRequestMessage requestMessage){
        System.out.println("DATA Request Calculating ...");

        int requestUsageAmount = requestMessage.getUsageAmount();
        double totalPrice = requestMessage.getTotalUsagePrice();
        long msisdn = Long.parseLong(requestMessage.getSenderMSISDN());


        var userDataBalance = voltOperation.getMinutesBalance(msisdn);
        var userWalletBalance = voltOperation.getMoneyBalance(msisdn);
        var uID = voltOperation.getUserID(msisdn);

        System.out.println("User ID: "+(int)uID);
        System.out.println("Request Amount: "+requestUsageAmount);
        System.out.println("Total Price: "+(int) totalPrice);
        System.out.println("DATA Balance: "+userDataBalance);
        System.out.println("Wallet Balance: "+userWalletBalance);

        if(userDataBalance <= 0){

            if(userWalletBalance <= 0){

                System.out.println("No Sufficient VOICE and WALLET Balance");

            } else if(userWalletBalance >= totalPrice){

                System.out.println("DATA request Used ** BALANCE **  Used");
                voltOperation.updateMoneyBalance(msisdn, -((int) totalPrice));
                //kafkaOperator.sendKafkaWalletMessage(msisdn, (int) uID, (int)totalPrice);
                System.out.println("*** DB SENT ***");

            } else if(userWalletBalance < totalPrice){

                System.out.println("No Sufficient WALLET Balance");

            }
        } else if(userDataBalance >= requestUsageAmount){
            System.out.println("DATA Request * NORMAL * Condition");

            voltOperation.updateDataBalance(msisdn, requestUsageAmount);
            //kafkaOperator.sendKafkaUsageMessage(requestMessage.getType(), msisdn, (int) uID, requestMessage.getUsageAmount());
            System.out.println("*** DB SENT ***");

        } else if(userDataBalance < requestUsageAmount){

            requestMessage.setUsageAmount(requestUsageAmount - userDataBalance);
            System.out.println("New Usage Amount: "+ (requestUsageAmount - userDataBalance));
            requestMessage.calculateTotalPrice();
            System.out.println("Calculated Total Price: "+ requestMessage.getTotalUsagePrice());

            System.out.println("DATA Request Used ** CALCULATED WALLET ** Balance");
            voltOperation.updateMoneyBalance(msisdn, -((int) requestMessage.getTotalUsagePrice()));
            //kafkaOperator.sendKafkaWalletMessage(msisdn, (int) uID, (int) requestMessage.getTotalUsagePrice());
            System.out.println("*** DB SENT ***");


            int remainingDataAmount =  userDataBalance;
            System.out.println("RemainingDataAmount: "+ remainingDataAmount);

            System.out.println("DATA Request Used ** CALCULATED VOICE ** Balance");
            voltOperation.updateVoiceBalance(msisdn, remainingDataAmount);
            //kafkaOperator.sendKafkaUsageMessage(requestMessage.getType(), msisdn, (int) uID, userDataBalance);
            System.out.println("*** DB SENT ***");
        }
    }

}