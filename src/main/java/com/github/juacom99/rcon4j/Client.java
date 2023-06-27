/*
 * Copyright (C) 2023 Joaquin Martinez <juacom04@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.juacom99.rcon4j;

import com.github.juacom99.rcon4j.exception.RCONInvalidAuthenticationException;
import com.github.juacom99.rcon4j.net.client.RCONClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jomartinez
 */
public class Client
{
    public static void main(String[] args)
    {
        
        try
        {
            RCONClient cli=new RCONClient();
            cli.connect(new InetSocketAddress(3002),"changeme");
            
            
            Scanner in=new Scanner(System.in);
            System.out.print("==>");
            String line=in.nextLine();
            while(!line.equals("quit"))
            {
                System.out.println("<=="+cli.exec(line));
                System.out.print("==>");
                line=in.nextLine();
            }
            
            
        } catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RCONInvalidAuthenticationException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
