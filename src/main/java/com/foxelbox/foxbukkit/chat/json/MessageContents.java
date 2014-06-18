/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.chat.json;

import com.foxelbox.foxbukkit.main.chat.Parser;

public class MessageContents {
    public MessageContents(String plain, String formatXML, String[] formatXMLArgs) {
        this.plain = plain;
        this.xml = String.format(formatXML, xmlEscapeArray(formatXMLArgs));
    }

    public MessageContents(String plain) {
        this.plain = plain;
    }

    private static String[] xmlEscapeArray(String[] in) {
        final String[] out = new String[in.length];
        for(int i = 0; i < in.length; i++)
            out[i] = Parser.escape(in[i]);
        return out;
    }

    public String plain;
    public String xml;
}
