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

package com.github.juacom99.rcon4j.net.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juacom99
 * 3	SERVERDATA_AUTH
 * 2	SERVERDATA_AUTH_RESPONSE
 * 2	SERVERDATA_EXECCOMMAND
 * 0	SERVERDATA_RESPONSE_VALUE
 * 
 */
public enum RCONPackageType
{
   SERVERDATA_AUTH(3),
   SERVERDATA_AUTH_RESPONSE(2),
   SERVERDATA_EXECCOMMAND(2),
   SERVERDATA_RESPONSE_VALUE(0);
   
   public final int value;
   
   private static final Map<Integer, RCONPackageType> BY_ID = new HashMap<>();
   
    static {
        for (RCONPackageType p: RCONPackageType.values()) {
            BY_ID.put(p.value, p);
        }
    }

    private RCONPackageType(int value) {
        this.value = value;
    }
    
    
     public static RCONPackageType valueOf(int type,boolean fromServer)
     {
         RCONPackageType ret;
         if(type==2)
         {
             if(fromServer)
             {
                ret=RCONPackageType.SERVERDATA_AUTH_RESPONSE;
             }
             else
             {
                 ret=RCONPackageType.SERVERDATA_EXECCOMMAND;
             }
         }
         else
         {
             ret=BY_ID.get(type);
         }
        return ret;
    }
}
