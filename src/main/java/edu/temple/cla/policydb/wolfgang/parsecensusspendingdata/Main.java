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
import java.util.StringJoiner;

/**
 * This program parses the Public Use File from the U S Census.
 *
 * For the years 2000 through 2015 the data files are available at
 * <a href="http://www2.census.gov/govs/state/xxstate35.txt">www2.census.gov/govs/state/xxstate35.txt</a> where xx is the year.
 * Format of this data is described in
 * <a href="https://www2.census.gov/programs-surveys/state/technical-documentation/file-layouts/public-use-file-layout.csv">www2.census.gov/programs-surveys/state/technical-documentation/file-layouts/public-use-file-layout.csv</a>
 * These files contained only national and state level data.
 *
 * For 2016 and presumably beyond data is available at:
 * <a href="https://www.census.gov/programs-surveys/gov-finances/data/datasets.html">www.census.gov/programs-surveys/gov-finances/data/datasets.html</a> The
 * data is available in a zip file of the form: xxxx_Individual_Unit_file.zip.
 * The data itself is in the file xxxxFinEstDAT_yyyyyyyymodp_pu.txt. The format
 * of the file is described in xxxx S&amp;L Indiv Unit Data File Tech Doc.pdf. This
 * file contains lower level government data as well as state level totals. xxxx
 * represents the year.
 *
 * The program inserts the data into the BudgetData table. Only items with a
 * code of the form xnn where x is the single letter representing the Object
 * Code and nn is the two digit number representing the function code.
 */
public class Main {

    private static final String STATE_IDS = "000000000000";
    private static final String FIND_MAX_ID
            = "select max(ID) from BudgetTable";
    private static final String FIND_MAX_YEAR
            = "select max(TheYear) from BudgetTable";

    /**
     * Main method.
     *
     * @param args the command line arguments 
     * <dl>
     * <dt>args[0]</dt><dd>The DataSource parameters file</dd>
     * <dt>args[1]</dt><dd>The input file name</dd>
     * <dt>args[2]</dt><dd>The state code (two digit number)</dd>
     * <dt>args[3]</dt><dd>The year (4 digit number) to determine the format</dd>
     * </dl>
     */
    public static void main(String[] args)  {

        SimpleDataSource dataSource = new SimpleDataSource(args[0]);
        String stateID = args[2] + STATE_IDS;
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                BufferedReader in = new BufferedReader(new FileReader(args[1]))) {
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
            BudgetData.setNextId(maxId + 1);
            int year = Integer.parseInt(args[3]);
            if (year > 2015) {
                BudgetData.setBuidgetDataBuilder(new NewFormatBudgetDataBuilder());
            } else {
                BudgetData.setBuidgetDataBuilder(new OldFormatBudgetDataBuilder());
            }
            StringBuilder insertStatement = new StringBuilder();
            insertStatement.append("insert into BudgetTable values\n");
            StringJoiner values = new StringJoiner(",\n");
            String line;
            boolean first = true;
            while ((line = in.readLine()) != null) {
                if (line.substring(0, 14).equals(stateID)) {
                    BudgetData budgetData = BudgetData.parseLine(line);
                    if (budgetData != null && budgetData.getTheYear() > maxYear) {
                        values.add(budgetData.toString());
                    }
                }
            }
            insertStatement.append(values);
            System.out.println(insertStatement);
            statement.executeUpdate(insertStatement.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
