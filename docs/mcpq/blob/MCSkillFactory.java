/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package server.partyquest.mcpq;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class MCSkillFactory {

    private static Map<Integer, MCSkill> mcSkills = new HashMap<>();
    private static Map<Integer, MCGuardian> mcGuardians = new HashMap<>();
    private static MapleDataProvider dataSource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Skill.wz"));
    private static MapleData mcSkillRoot = dataSource.getData("MCSkill.img");
    private static MapleData mcGuardianRoot = dataSource.getData("MCGuardian.img");
    private static ReentrantReadWriteLock skillLock = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock guardianLock = new ReentrantReadWriteLock();

    public static MCSkill getMCSkill(int skillId) {
        skillLock.readLock().lock();
        try {
            MCSkill ret = mcSkills.get(skillId);
            if (ret != null) {
                return ret;
            }
        } finally {
            skillLock.readLock().unlock();
        }
        skillLock.writeLock().lock();
        try {
            MCSkill ret;
            ret = mcSkills.get(skillId);
            if (ret == null) {
                MapleData skillData = mcSkillRoot.getChildByPath(String.valueOf(skillId));
                if (skillData != null) {
                    int target = MapleDataTool.getInt("target", skillData, 0);
                    int spendCP = MapleDataTool.getInt("spendCP", skillData, 0);
                    int mobSkillID = MapleDataTool.getInt("mobSkillID", skillData, 0);
                    int level = MapleDataTool.getInt("level", skillData, 0);
                    ret = new MCSkill(skillId, target, mobSkillID, level, spendCP);

                    if (MonsterCarnival.DEBUG) {
                        String name = MapleDataTool.getString("name", skillData, "");
                        String desc = MapleDataTool.getString("desc", skillData, "");
                        ret.setName(name);
                        ret.setDesc(desc);
                    }
                    mcSkills.put(skillId, ret);
                }
            }
            return ret;
        } finally {
            skillLock.writeLock().unlock();
        }
    }

    public static MCGuardian getMCGuardian(int id) {
        guardianLock.readLock().lock();
        try {
            MCGuardian ret = mcGuardians.get(id);
            if (ret != null) {
                return ret;
            }
        } finally {
            guardianLock.readLock().unlock();
        }
        guardianLock.writeLock().lock();
        try {
            MCGuardian ret;
            ret = mcGuardians.get(id);
            if (ret == null) {
                MapleData skillData = mcGuardianRoot.getChildByPath(String.valueOf(id));
                if (skillData != null) {
                    int type = MapleDataTool.getInt("type", skillData, 0);
                    int spendCP = MapleDataTool.getInt("spendCP", skillData, 0);
                    int mobSkillID = MapleDataTool.getInt("mobSkillID", skillData, 0);
                    int level = MapleDataTool.getInt("level", skillData, 0);
                    ret = new MCGuardian(type, spendCP, mobSkillID, level);

                    if (MonsterCarnival.DEBUG) {
                        String name = MapleDataTool.getString("name", skillData, "");
                        String desc = MapleDataTool.getString("desc", skillData, "");
                        ret.setName(name);
                        ret.setDesc(desc);
                    }
                    mcGuardians.put(type, ret);
                }
            }
            return ret;
        } finally {
            guardianLock.writeLock().unlock();
        }
    }
}  
