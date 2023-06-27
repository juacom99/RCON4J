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

import com.github.juacom99.rcon4j.net.server.RCONServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juacom99
 */
public class Server {

    public static void main(String[] args) {
       
        try
        {
            RCONServer srv=new RCONServer("changeme");
            InetSocketAddress bindAddr=new InetSocketAddress(14884);
            srv.start(bindAddr);
        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
