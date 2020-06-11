package projectcrc;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * CRC Project
 * @author Savvas Theofilou
 */
public class ProjectCRC {
    
    /**
     * This method reads a number P from the user.
     * @return the number that the user put.
     */
    public static ArrayList<Integer> readNumberP(){
        ArrayList<Integer> tempList=new ArrayList<>();
        String number;
        Scanner scanner=new Scanner(System.in);
        
        System.out.print("Give a binary number P: ");
        number=scanner.nextLine();
        for (int i=0;i<number.length();i++){
            tempList.add(Integer.parseInt(Character.toString(number.charAt(i))));
        }
        
        return tempList;
    }
    
    /**
     * This method finds the first remainder(FCS)
     * @param message The message consisting of k bits
     * @param divisor The divisor consisting of n bits
     * @return the remainder consisting of n-1 bits
     */
    public static ArrayList<Integer> findFirstRemainder(ArrayList<Integer> message,ArrayList<Integer> divisor){
        ArrayList<Integer> tempMessage=new ArrayList<>();
        tempMessage.addAll(message);
        ArrayList<Integer> result=new ArrayList<>();
        boolean ok=false;
        boolean first=true;
        
        for (int i=0;i<divisor.size()-1;i++){
            tempMessage.add(0);
        }
        for (int i=0;i<divisor.size();i++){
            result.add(tempMessage.get(i));
        }
        do{
            int removed=0;
            //XOR
            for (int i=0;i<divisor.size();i++){
                result.set(i, xorNumbers(result.get(i),divisor.get(i)));
                if (first){
                    tempMessage.remove(0);
                }
            }
            first=false;
            //REMOVE ZEROS
            while(result.get(0)==0){
                result.remove(0);
                removed++;
                if (result.isEmpty()){
                    break;
                }
            }
            //ADD NEXT BITS
            while (result.size()<divisor.size()){
                if (!tempMessage.isEmpty()){
                    result.add(tempMessage.get(0));
                    tempMessage.remove(0);
                }
                else{
                    if (result.size()<divisor.size()){
                        ok=true;
                        break;
                    }
                }
            }
            while (result.size()<divisor.size()-1){
                result.add(0,0);
            }
        }
        while(!ok);
        
        return result;
    }
    
    /**
     * This method finds the remainder
     * @param message The message consisting of k bits
     * @param divisor The divisor consisting of n bits
     * @return the remainder consisting of n-1 bits
     */
    public static ArrayList<Integer> findRemainder(ArrayList<Integer> message,ArrayList<Integer> divisor){
        ArrayList<Integer> tempMessage=new ArrayList<>();
        tempMessage.addAll(message);
        ArrayList<Integer> result=new ArrayList<>();
        boolean ok=false;
        boolean first=true;
        
        for (int i=0;i<divisor.size();i++){
            result.add(tempMessage.get(i));
        }
        do{
            int removed=0;
            //XOR
            for (int i=0;i<divisor.size();i++){
                result.set(i, xorNumbers(result.get(i),divisor.get(i)));
                if (first){
                    tempMessage.remove(0);
                }
            }
            first=false;
            //REMOVE ZEROS
            while(result.get(0)==0){
                result.remove(0);
                removed++;
                if (result.isEmpty()){
                    break;
                }
            }
            //ADD NEXT BITS
            while (result.size()<divisor.size()){
                if (!tempMessage.isEmpty()){
                    result.add(tempMessage.get(0));
                    tempMessage.remove(0);
                }
                else{
                    ok=true;
                    break;
                }
            }
            while (result.size()<divisor.size()-1){
                result.add(0,0);
            }
        }
        while(!ok);
        
        return result;
    }
    
    /**
     * This method returns the result of a logical XOR operation between two bits
     * @param bit1 first bit
     * @param bit2 second bit
     * @return result of XOR operation
     */
    public static Integer xorNumbers(Integer bit1,Integer bit2){
        if (bit1.equals(bit2)){
            return 0;
        }
        return 1;
    }
    
    /**
     * This method finds if a message was sent with an error or not
     * @param remainderBits the remainder of the division that was made
     * @return true if finds an error, false if not
     */
    public static boolean foundError(ArrayList<Integer> remainderBits){
        for (int i=0;i<remainderBits.size();i++){
            if (remainderBits.get(i)!=0){
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method checks if two messages are the same
     * @param original the original message
     * @param sendMessage the message that was sent
     * @return true if they are NOT the same, false if they are the same
     */
    public static boolean messagesNotSame(ArrayList<Integer> original,ArrayList<Integer> sendMessage){
        for (int i=0;i<original.size();i++){
            if (!original.get(i).equals(sendMessage.get(i))){
                return true;
            }
        }
        return false;
    }
    
    public static void main(String[] args) {
        int totalMessages=1000000;
        Random randomNumber=new Random();
        ArrayList<Integer> numP=new ArrayList<>();
        ArrayList<Integer> finalResult=new ArrayList<>();
        ArrayList<Integer>[] messages=new ArrayList[totalMessages];
        ArrayList<Integer>[] allFCS=new ArrayList[totalMessages];
        ArrayList<Integer>[] messagesOriginal=new ArrayList[totalMessages];
        int kBits=10,countOfErrors=0,countFalseChecks=0;
        double bitErrorRate=0.001;
        
        System.out.println("///Error detection by CRC///");
        System.out.println();
        System.out.println("Number of messages: "+totalMessages);
        System.out.println("Bits of messages: "+kBits);
        System.out.println("Bit Error Rate(E): "+bitErrorRate);
        System.out.println();
        numP=readNumberP();
        
        //Creates random messages consisting of 10 bits
        //Finds FCS for every message
        //Adds FCS to every message 
        for (int i=0;i<messages.length;i++){
            messages[i]=new ArrayList<>();
            for (int j=0;j<kBits;j++){
                messages[i].add(randomNumber.nextInt(2));
            }
            allFCS[i]=findFirstRemainder(messages[i],numP);
            messages[i].addAll(allFCS[i]);
        }
        
        //Save original messages before sent
        for (int i=0;i<messages.length;i++){
            messagesOriginal[i]=new ArrayList<>();
            messagesOriginal[i].addAll(messages[i]);
        }
        
        //"Sends" message to receiver with a certain bit error rate
        for (int i=0;i<messages.length;i++){
            for (int j=0;j<messages[i].size();j++){
                double random=randomNumber.nextDouble();
                if (random<bitErrorRate){
                    if (messages[i].get(j)==1){
                        messages[i].set(j, 0);
                    }
                    else{
                        messages[i].set(j, 1);
                    }
                }
            }
        }
        
        for (int i=0;i<messages.length;i++){
            finalResult=findRemainder(messages[i],numP);
            if (foundError(finalResult)){
                countOfErrors++;
            }
            else{
                if (messagesNotSame(messagesOriginal[i],messages[i])){
                    countFalseChecks++;
                }
            }
            finalResult.clear();
        }
        System.out.println(countOfErrors+" messages out of "+messages.length+" have errors! ("+((double)countOfErrors/(double)messages.length)*100+"%)");
        System.out.println(countFalseChecks+" messages out of "+messages.length+" have errors but not recognised! ("+((double)countFalseChecks/(double)messages.length)*100+"%)");
    }
    
}
