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

import java.util.Objects;

/**
 * This class represents a row of the BudgetTable
 *
 * @author Paul Wolfgang
 */
public class BudgetData {

    private int id;                   //The primary key
    private final String oc;          //The Object Code
    private final int fc;             //The Function Code
    private final int theYear;        //The year
    private final int theValue;       //The ammount in $1000 units

    private static int nextId; //The next id value to assign.
    private static BudgetDataBuilder builder;

    /**
     * Package Private constructor.
     */
    BudgetData(String oc, int fc, int theYear, int theValue) {
        this.oc = oc;
        this.fc = fc;
        this.theYear = theYear;
        this.theValue = theValue;
    }

    
    /**
     * Method to set the initial nextId;
     * @param theNextId;
     */
    public static void setNextId(int theNextId) {
        nextId = theNextId;
    }
    
    /**
     * Method to set the BudgeDataBuilder.
     * @param theBuilder The BudgetDataBuilder
     */
    public static void setBuidgetDataBuilder(BudgetDataBuilder theBuilder) {
        builder = theBuilder;
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
        BudgetData budgetData = builder.parseLine(line);
        if (budgetData != null) {
            budgetData.id = ++nextId;
        }
        return budgetData;
    }

    /**
     * Generate a string representation that is in the format for insertion into
     * the database.
     *
     * @return A value line for the insert statement.
     */
    @Override
    public String toString() {
        return String.format("(%d, '%s', %d, %s, %d, %d)",
                id, oc, fc, "NULL", theYear, theValue);
    }
    
    /**
     * Return the year value.
     * @return theYear
     */
    public int getTheYear() {
        return theYear;
    }
    
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() == o.getClass()) {
            BudgetData other = (BudgetData) o;
            return oc.equals(other.oc) &&
                    fc == other.fc &&
                    theYear == other.theYear &&
                    theValue == other.theValue;            
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.oc);
        hash = 59 * hash + this.fc;
        hash = 59 * hash + this.theYear;
        hash = 59 * hash + this.theValue;
        return hash;
    }

}
