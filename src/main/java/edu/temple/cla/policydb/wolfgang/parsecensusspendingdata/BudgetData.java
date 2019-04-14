/* 
 * Copyright (c) 2019, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.temple.cla.policydb.wolfgang.parsecensusspendingdata;

/**
 * This class represents a row of the BudgetTable
 *
 * @author Paul Wolfgang
 */
public class BudgetData {

    private int id;             //The primary key
    private String oc;          //The Object Code
    private int fc;             //The Function Code
    private String re;          //The real/exhibit flag
    private int theYear;        //The year
    private int theValue;       //The ammount in $1000 units

    private static int nextId; //The next id value to assign.

    /**
     * Private constructor.
     */
    private BudgetData() {
    }

    ;
    
    /**
     * Method to set the initial nextId;
     * @param theNextId;
     */
    public static void setNextId(int theNextId) {
        nextId = theNextId;
    }

    /**
     * Method to parse a line from the census data and create a new BudgetData
     * object.
     *
     * @param line A line from the census data
     * @return A new BudgetData object, or null if the code is not in the
     * desired format.
     */
    public static BudgetData parseLine(String line) {
        String itemCode;
        String amount;
        String dataYear;

        itemCode = line.substring(14, 17);
        amount = line.substring(17, 32).trim();
        dataYear = line.substring(34, 36);
        if (Character.isAlphabetic(itemCode.charAt(0))
                && Character.isDigit(itemCode.charAt(1))
                && Character.isDigit(itemCode.charAt(2))) {
            BudgetData budgetData = new BudgetData();
            try {
                budgetData.id = nextId++;
                budgetData.oc = Character.toString(itemCode.charAt(0));
                budgetData.fc = Integer.parseInt(itemCode.substring(1, 3));
                int year = Integer.parseInt(dataYear);
                if (year > 70) {
                    year += 1900;
                } else {
                    year += 2000;
                }
                budgetData.theYear = year;
                if (amount.isEmpty()) {
                    budgetData.theValue = 0;
                } else {
                    budgetData.theValue = Integer.parseInt(amount.trim());
                }
            } catch (Exception ex) {
                System.err.println("Error parsing line: " + line);
                throw ex;
            }
            return budgetData;
        } else {
            return null;
        }
    }

    /**
     * Generate a string representation that is in the format for insertion into
     * the database.
     *
     * @return A value line for the insert statement.
     */
    @Override
    public String toString() {
        return String.format("(%d, '%s', %d, %s, %d, %d)", getId(), getOc(), getFc(), "NULL", getTheYear(), getTheValue());
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the oc
     */
    public String getOc() {
        return oc;
    }

    /**
     * @return the fc
     */
    public int getFc() {
        return fc;
    }

    /**
     * @return the re
     */
    public String getRe() {
        return re;
    }

    /**
     * @return the theYear
     */
    public int getTheYear() {
        return theYear;
    }

    /**
     * @return the theValue
     */
    public int getTheValue() {
        return theValue;
    }

}
