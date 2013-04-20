/*********
 * CS3251 Networking project
 * Matt Hawkins, Kurtis Nelson, Bryan Sills, Henry Smith, Jay Zuerndorfer
 * 
 * Class to interface with our TC bash scripts
 */
package com.kelsonprime.trafficshape;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class TC {
        private String eth;
        private int level;
        private int rate;
        String envp[];
        String rules[];

        public static void main(String[] args) {
          TC tc = new TC("eth0");
          tc.up();
//          tc.down();
        }

        public void setRate(int rate) {
          int oldRate = this.rate;
          this.rate = rate;

          for(int i = 0; i < rules.length; i++){
                  if(rules[i] != null && rules[i].length() > 1)
                          rules[i].replaceAll(oldRate + "", rate + "");
          }
          int oldLevel = level;
          while(level > 0)
                  down();
          while(level < oldLevel)
                  up();
        }

        public TC(String eth) {
                this.eth = eth;
                level = 0;
                envp = new String[1];
                envp[0] = "PATH=" + System.getProperty("java.library.path");
                rate = 2560;
                loadRules();
        }

        public void up() {
                level++;
                if(level == 1) {
                    for(int i = 0; i < rules.length; i++){
                            if(rules[i] != null && rules[i].length() > 1)
                                    exec(rules[i].replaceAll("256000", rate + ""));
                    }
                }
        }

        public void down() {
                if(level > 1){
                        level--;
                }else{
                        level = 0;
                        reset();
                }
        }


        private void exec(String cmd) {
                try {
                        Runtime.getRuntime().exec(cmd, envp);
                        System.out.println(cmd);
                } catch (Exception e) {
                        System.out.println("Error: "+ cmd);
                }
        }

        public void reset() {
                exec("tc qdisc del dev " + eth + " root");
        }

        private void loadRules() {
                try {
                File init = new File("tc-init");
                Scanner scanner = new Scanner(init);
                int count = 0;
                rules = new String[100];
                while (scanner.hasNextLine()) {
                        rules[count] = scanner.nextLine().replaceAll("enp2s1", eth);
                        count++;
                }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("ERRROR ERROR ERROR");
                }
        }
}
