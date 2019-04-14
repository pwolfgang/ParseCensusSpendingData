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

import edu.temple.cla.policydb.dbutilities.SimpleDataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This program parses the Public Use File from the U. S. Census.
 * Source of the data is http://www2.census.gov/govs/state/xxstate35.txt
 * where xx is the year.  Format of this data is described in
 * http://www.census.gov/govs/state/public_use_file_layout.html.
 * The data is inserted into the PAPolicy.BudgetData table. Only data for
 * Pennsylvania (government code 39) is inserted. Also only items with
 * a code of the form xnn where x is a single letter representing the
 * Object Code and nn is a two digit number representing the function code.
 * @author Paul Wolfgang
 */
public class Main {
    
    private static final String PENNSYLVANIA = "39000000000000";
    private static final String FIND_MAX_ID =
            "select max(ID) from BudgetTable";
    private static final String FIND_MAX_YEAR =
            "select max(TheYear) from BudgetTable";

    /**
     * Main method.
     * @param args the command line arguments
     * args[0] is the datasource
     * args[1] is the name of the input file
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        SimpleDataSource dataSource = new SimpleDataSource(args[0]);
        try (Connection connection = dataSource.getConnection(); 
                Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(FIND_MAX_YEAR);
            int maxYear;
            if (rs.next()) {
                maxYear = rs.getInt(1);
            } else {
                throw new Exception("Failed to read max year from db");
            }
            rs.close();
            rs = statement.executeQuery(FIND_MAX_ID);
            int maxId;
            if (rs.next()) {
                maxId = rs.getInt(1);
            } else {
                throw new Exception("Failed to read max id from dg");
            }
            rs.close();
            System.out.printf("maxYear: %d, maxId: %d%n", maxYear, maxId);
            BudgetData.setNextId(maxId+1);
            StringBuilder insertStatement = new StringBuilder();
            insertStatement.append("insert into BudgetTable values\n");
            BufferedReader in = new BufferedReader(new FileReader(args[1]));
            String line;
            boolean first = true;
            while ((line = in.readLine()) != null) {
                if (line.substring(0, 14).equals(PENNSYLVANIA)) {
                    BudgetData budgetData = BudgetData.parseLine(line);
                    if (budgetData != null && budgetData.getTheYear() > maxYear) {
                        if (!first) insertStatement.append(",\n");
                        else first = false;
                        insertStatement.append(budgetData);
                    }
                }
            }
            System.out.println(insertStatement);
            statement.executeUpdate(insertStatement.toString());
        }
    }
        
    
}
