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
 *
 * @author Paul
 */
public class OldFormatBudgetDataBuilder implements BudgetDataBuilder {
    
    @Override
    public BudgetData parseLine(String line) {
        String itemCode;
        String amount;
        String dataYear;

        itemCode = line.substring(14, 17);
        amount = line.substring(17, 32).trim();
        dataYear = line.substring(34, 36);
        if (Character.isAlphabetic(itemCode.charAt(0))
                && Character.isDigit(itemCode.charAt(1))
                && Character.isDigit(itemCode.charAt(2))) {
            try {
                String oc = Character.toString(itemCode.charAt(0));
                int fc = Integer.parseInt(itemCode.substring(1, 3));
                int year = Integer.parseInt(dataYear);
                if (year > 70) {
                    year += 1900;
                } else {
                    year += 2000;
                }
                int theYear = year;
                int theValue;
                if (amount.isEmpty()) {
                    theValue = 0;
                } else {
                    theValue = Integer.parseInt(amount.trim());
                }
                return new BudgetData(oc, fc, theYear, theValue);
            } catch (Exception ex) {
                System.err.println("Error parsing line: " + line);
                throw ex;
            }
        } else {
            return null;
        }
        
    }
    
}
