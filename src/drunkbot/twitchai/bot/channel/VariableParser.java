package drunkbot.twitchai.bot.channel;

import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.Globals;

import java.util.ArrayList;

/**
 * Created by Kevin on 19/08/2016.
 */
public class VariableParser
{
   public static String parse(TwitchChannel channel, String message) {
       ArrayList<String> variables = new ArrayList<>();
       for (int i = 0; i < message.length() - 1; i++) {
           char c = message.charAt(i);
           char nextC = message.charAt(i+1);
           // Potential start of variable
           if (c == '{' && nextC == '{') {
               StringBuilder sb = new StringBuilder();

               // Add variable to variables list
               for (int j = i; j < message.length() - 1; j++) {
                   char cChar = message.charAt(j);
                   char nChar = message.charAt(j+1);
                   sb.append(cChar);
                   if (cChar == '}' && nChar == '}') {
                       sb.append(nChar);
                       variables.add(sb.toString().toUpperCase());
                       break;
                   }
               } // for-loop for variable
           } // end if for start of variable
       } // end message char iteration
       return replaceVariables(channel, variables, message);
   } // end parse

   private static String replaceVariables(TwitchChannel channel, ArrayList<String> variables, String message) {
       String replacedMessage = message;
       for (int i = 0; i < variables.size(); i++) {
           String variable = variables.get(i);
           if (variable.length() > 4) {
               variable = variable.replaceAll("[{}]", "");
               replacedMessage = replacedMessage.replaceAll("[{}]", "");
           }
           switch (variable) {
               case "CURRENCY_AMOUNT":
                   double amountGenerated;
                   if (channel.getTwitchAPI().isOnline(channel.getCurrencyManager().getGenerateInterval()))
                   {
                       amountGenerated = channel.getCurrencyManager().getGenerateAmount();
                   } else {
                       amountGenerated = channel.getCurrencyManager().getOfflineGenerateAmount();
                   }

                   replacedMessage = replacedMessage.replaceAll(variable, Globals.g_currencyFormat.format(amountGenerated));
                   break;
               case "CURRENCY_INTERVAL":
                   replacedMessage = replacedMessage.replaceAll(variable, String.valueOf(channel.getCurrencyManager().getGenerateInterval()/1000));
                   break;
           }

       }
       return replacedMessage;
   }

}
