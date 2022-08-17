import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Reservation 
{
 
      private int seats;
      private String name;
      private Date date;
      private String inputString;

      public Reservation(String nameParam, int seatsParam, Date dateParam, String inputStringParam) {
            name = nameParam;
            seats = seatsParam;
            date = dateParam;
            inputString = inputStringParam;
      }

      public String getName() {
            return name;
      }

      public int getSeats() {
            return seats;
      }

      public Date getDate() {
            return date;
      }

      public Map<String,Object> getAllData() {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name",name);
            map.put("date", date);
            map.put("seats", seats);
            map.put("inputString",inputString);
            return map;
      }

       public static void main (String[] args) throws ParseException
       {
            
            List<String> inputStrings = new ArrayList<String>();
            List<Reservation> reservations = new ArrayList<Reservation>();
           if(args.length == 0) {
                  inputStrings.add("Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller");
                  inputStrings.add("Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier");
                  inputStrings.add("Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze");
                  inputStrings.add("Guten Tag, einen Tisch für zwanzig Mann am 1.5. 12 Uhr mittags, Gruß Franz Schulze");

            } else {
                  for (int i=0; i<args.length; i++) {
                        inputStrings.add(args[i]);
                  }   
            }
            
            for (String inputString : inputStrings) {                              
                  String nameTemp = "";
                  int seatsTemp = 0;
                  Date dateTemp= null;

                  try {
                        nameTemp = extractName(inputString);

                  } catch (Exception e) {
                        e.printStackTrace();
                  }  
                  try {
                        dateTemp = extractDate(inputString);

                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  try {
                        seatsTemp = extractSeats(inputString);
                  } catch (Exception e) {
                        e.printStackTrace();
                  }     
                  Reservation rsrvtn = new Reservation(nameTemp, seatsTemp, dateTemp, inputString);                         
                  reservations.add(rsrvtn);
            }  
            for (Reservation reservationTemp : reservations) {
                  System.out.println(reservationTemp.getAllData());
            }
       }

       /*
        * Extracts String name of the person doing the reservation from String input
        * @param  input the String containing the reservation and the name of the person that made the reservation
        * @return the extracted String name of the person that made the reservation
        */
       private static String extractName (String input) throws Exception
       {
            String name = "";
            String[] splittedInput = null;
            if (input.contains("Danke")) {
                  splittedInput = input.split("Danke");
            } else if (input.contains("Dank")) {
                  splittedInput = input.split("Dank");
            } else if (input.contains("Grüßen")) {
                  splittedInput = input.split("Grüßen");
            } else if (input.contains("Grüße")) {
                  splittedInput = input.split("Grüße");
            } else if (input.contains("Gruß")) {
                  splittedInput = input.split("Gruß");
            } else if (input.toLowerCase().contains("bis dann")) {
                  splittedInput = input.split("bis dann");
            } else if (input.toLowerCase().contains("bis später")) {
                  splittedInput = input.split("bis später");
            }

            if (splittedInput != null && splittedInput[1].length() > 0) {                  
                  name = splittedInput[1].trim();
            }else{
                  throw new Exception("Could not extract name");
            }

            // Alternative approach: Use NLP library from stanford (NERTagger / NERClassifier) to detect human names and then extract these  

            return name;
       }

       /*
        * Extracts int seats of with the amount of seats requested in the reservation from String input
        * @param  input the String containing the reservation and the amount of seats requested in the reservation
        * @return the extracted int seats of with the amount of seats requested in the reservation
        */
        private static int extractSeats (String input) throws Exception
        {
             int seats = 0;
             String substr = "";
             String inputLC = input.toLowerCase();
             if (inputLC.contains("person")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("person")); //input.split("person");
             } else if (inputLC.contains("personen")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("personen"));
             } else if (inputLC.contains("leute")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("leute"));
             } else if (inputLC.contains("mann")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("mann"));
             } else if (inputLC.contains("gast")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("gast"));
             } else if (inputLC.contains("gäste")) {
                  substr = inputLC.substring( 0, inputLC.indexOf("gäste"));
             }

             if (substr != "") {
                  substr = substr.trim();
                  String numberWord = substr.substring(substr.lastIndexOf(" ")+1);
                  if (numberWord.matches("\\d+")) {
                        seats = Integer.parseInt(numberWord);
                  }else{
                        seats = wordToInt(numberWord);
                  }
             }else{
                   throw new Exception("Could not extract seats");
             }
             return seats;
        }

       /*
        * Extracts Date date of the reservation from String input
        * @param  input the String containing the reservation and the date of the reservation
        * @return the extracted Date date of the reservation
        */
       private static Date extractDate (String input) throws Exception
       {      
            // find date, e.g. 15. März
            String regex1 = "(\\d{1,2})\\.\\s?(januar|februar|märz|april|mai|juni|juli|august|september|oktober|november|dezember|jan|feb|mär|apr|jun|jul|aug|sep|okt|nov|dez)";
            Matcher m1 = Pattern.compile(regex1).matcher(input.toLowerCase());
            // find date, e.g. 15.3
            String regex2 = "(\\d{1,2})\\.(\\d{1,2})";
            Matcher m2 = Pattern.compile(regex2).matcher(input);
            
            // find time, e.g. 20:30
            String regex3 = "([0-1]?[0-9]|2[0-3]):([0-5][0-9])";
            Matcher m3 = Pattern.compile(regex3).matcher(input.toLowerCase());
            // find date, e.g. 20 Uhr
            String regex4 = "(\\d+)[^\\d]+[uU]hr";
            Matcher m4 = Pattern.compile(regex4).matcher(input.toLowerCase());

            String thisYear = new SimpleDateFormat("yyyy").format(new Date());
            Date date;

            String dateStr = "";
            if (m1.find() && m1.group(1).length() > 0 && m1.group(2).length() > 0) {
                  String monthName = m1.group(2);
                  
                  String monthNr = "";
                  try {
                        monthNr = getMonthNumberbyName(monthName);
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  dateStr = m1.group(1)+"."+monthNr+"."+thisYear;
            }else if (m2.find() && m2.group(1).length() > 0 && m2.group(2).length() > 0){
                  dateStr = m2.group(1)+"."+m2.group(2)+"."+thisYear;
            }else{
                  throw new Exception ("Could not extract date");
            }


            String timeStr = "";
            Boolean pm = false;
            if (m3.find()){
                  timeStr = " "+m3.group();
            }else if (m4.find() && m4.group(1).length() > 0) {
                  //search for daytime keywords to know whether its am / pm                 
                  if (Integer.parseInt(m4.group(1)) <= 12) {
                        // if in between 9 and 11 pm
                        if ((Integer.parseInt(m4.group(1)) >= 9 && Integer.parseInt(m4.group(1)) < 12) && input.toLowerCase().contains("nacht")) {
                              pm = true;
                        } else if (input.toLowerCase().contains("nachmittag")) {
                              pm = true;
                        } else if (input.toLowerCase().contains(" mittag")) {
                              pm = true;
                        } else if (input.toLowerCase().contains("abend")) {
                              pm = true;
                        }

                        if (Integer.parseInt(m4.group(1)) == 12) {
                              if (input.toLowerCase().contains("nachmittag") || input.toLowerCase().contains(" mittag")|| input.toLowerCase().contains("abend") ){
                                    timeStr = " 12:00";
                              } else if (input.toLowerCase().contains("am morgen") || input.toLowerCase().contains("morgens") || input.toLowerCase().contains("morgends") || input.toLowerCase().contains("nacht")){
                                    timeStr = " 00:00";
                              }
                        }else if (Integer.parseInt(m4.group(1)) != 12) {
                              if (pm == true) {
                                    int timeInt = Integer.parseInt(m4.group(1));
                                    timeInt += 12;	  
                                    timeStr = " "+Integer.toString(timeInt)+":00";    
                              }else{
                                    timeStr = " "+m4.group(1)+":00";
                              }
                        }
                  }else{
                        // if 24 hour format
                        timeStr = " "+m4.group(1)+":00";
                  }

            } else{
                  throw new Exception ("Could not extract time");
            }
            
            String dateFormat = "dd.MM.yyyy HH:mm";
            if (timeStr == "") dateFormat = "dd.MM.yyyy";
            date = new SimpleDateFormat(dateFormat).parse(dateStr+timeStr);

            return date;
       }

       private static String getMonthNumberbyName(String monthName) throws Exception{
            String result = "";
            
            switch (monthName)
            {
                  case("januar"): 
                  case("jan"): 
                  result = "1"; 
                  break;
                  case("februar"): 
                  case("feb"):
                  result = "2"; 
                  break;
                  case("märz"): 
                  case("mär"):
                  result = "3"; 
                  break;
                  case("april"): 
                  case("apr"):
                  result = "4"; 
                  break;
                  case("mai"): 
                  result = "5"; 
                  break;
                  case("juni"): 
                  case("jun"):
                  result = "6"; 
                  break;
                  case("juli"): 
                  case("jul"):
                  result = "7"; 
                  break;
                  case("august"): 
                  case("aug"):
                  result = "8"; 
                  break;
                  case("september"): 
                  case("sep"):
                  result = "9"; 
                  break;
                  case("oktober"): 
                  case("okt"):
                  result = "10"; 
                  break;
                  case("november"): 
                  case("nov"):
                  result = "11"; 
                  break;
                  case("dezember"): 
                  case("dez"): 
                  result = "12"; 
                  break;
                  default:
                        throw new Exception("Month name not found");
            }
            return result;
       }

       private static int wordToInt (String word) {
            int wordAsInt = 0;
            
            word = word.toLowerCase();

            if (word.contains("und") == false) {
                  switch (word) 
                  {
                        case("eins"): 
                        wordAsInt = 1; 
                        break;
                        case("zwei"): 
                        wordAsInt = 2; 
                        break;
                        case("drei"): 
                        wordAsInt = 3; 
                        break;
                        case("vier"): 
                        wordAsInt = 4; 
                        break;
                        case("fünf"): 
                        wordAsInt = 5; 
                        break;
                        case("sechs"): 
                        wordAsInt = 6; 
                        break;
                        case("sieben"): 
                        wordAsInt = 7; 
                        break;
                        case("acht"): 
                        wordAsInt = 8; 
                        break;
                        case("neun"): 
                        wordAsInt = 9; 
                        break;
                        case("zehn"): 
                        wordAsInt = 10; 
                        break;
                        case("elf"): 
                        wordAsInt = 11; 
                        break;
                        case("zwölf"): 
                        wordAsInt = 12; 
                        break;
                        case("dreizehn"): 
                        wordAsInt = 13; 
                        break;
                        case("vierzehn"): 
                        wordAsInt = 14; 
                        break;
                        case("fünfzehn"): 
                        wordAsInt = 15; 
                        break;
                        case("sechzehn"): 
                        wordAsInt = 16; 
                        break;
                        case("siebzehn"): 
                        wordAsInt = 17; 
                        break;
                        case("achtzehn"): 
                        wordAsInt = 18; 
                        break;
                        case("neunzehn"): 
                        wordAsInt = 19; 
                        break;
                        case("zwanzig"): 
                        wordAsInt = 20; 
                        break;
                        case("dreißig"): 
                        wordAsInt = 30; 
                        break;
                        case("vierzig"): 
                        wordAsInt = 40; 
                        break;
                        case("fünfzig"): 
                        wordAsInt = 50; 
                        break;
                        case("sechzig"): 
                        wordAsInt = 60; 
                        break;
                        case("siebzig"): 
                        wordAsInt = 70; 
                        break;
                        case("achzig"): 
                        wordAsInt = 80; 
                        break;
                        case("neunzig"): 
                        wordAsInt = 90; 
                        break;
                        case("hundert"): 
                        case("einhundert"): 
                        wordAsInt = 100; 
                        break;
                  }
            }else if (word.contains("und") == true) {
                  String[] words = word.split("und");
                  int nachUnd = wordToInt(words[1]);
                  int vorUnd = wordToInt(words[0]);
                  String kombinZahl = "";
                  if (nachUnd > 19 && vorUnd > 0) {
                        String nachUndInt = String.valueOf(nachUnd);
                        kombinZahl = nachUndInt.substring(0, nachUndInt.length() - 1)+""+String.valueOf(vorUnd);
                  }
                  if (kombinZahl.length() > 0) wordAsInt = Integer.parseInt(kombinZahl);
            }
            return wordAsInt;
       }
}