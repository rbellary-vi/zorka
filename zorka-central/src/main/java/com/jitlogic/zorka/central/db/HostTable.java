package com.jitlogic.zorka.central.db;

import com.jitlogic.zorka.central.CentralConfig;
import com.jitlogic.zorka.central.jedi.JediEntityProxy;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2012-2013 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

public class HostTable implements JediEntityProxy<DbRecord> {

    private DbContext db;
    private JdbcTemplate jdbc;

    private DbTableDesc tdesc;


    public HostTable(CentralConfig config, DbContext db) {
        this.db = db;
        this.jdbc = this.db.getJdbcTemplate();
        this.tdesc = db.getNamedDesc("HOSTS");

    }

    @Override
    public List<DbRecord> list(Map<String, String> params) {
        return jdbc.query("select * from HOSTS order by HOST_NAME", tdesc);
    }


    @Override
    public DbRecord get(String id, Map<String, String> params) {
        List<DbRecord> lst = jdbc.query("select * from HOSTS where HOST_ID = ?",
                new Object[] { Integer.parseInt(id) }, tdesc);
        return lst.size() > 0 ? lst.get(0) : null;
    }


    public DbRecord getHost(String hostname, String hostaddr) {

        List<DbRecord> lst = jdbc.query("select HOST_ID,HOST_NAME,HOST_PATH from HOSTS where HOST_NAME = ?",
                new Object[]{ hostname }, tdesc);

        if (lst.size() == 0) {
            jdbc.update("insert into HOSTS (HOST_NAME,HOST_ADDR,HOST_PATH) values (?,?,?)",
                hostname, hostaddr, safePath(hostname));
            return getHost(hostname, hostaddr);
        } else {
            return lst.get(0);
        }
    }


    private String safePath(String hostname) {
        return hostname;
    }
}
