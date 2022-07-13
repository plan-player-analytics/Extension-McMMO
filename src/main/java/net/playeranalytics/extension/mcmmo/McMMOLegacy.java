/*
 * Copyright(c) 2020 AuroraLS3
 *
 * The MIT License(MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files(the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions :
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.playeranalytics.extension.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class McMMOLegacy implements McMMO {

    @SuppressWarnings("deprecation") // deprecation comes from modern
    @Override
    public int getLevelOnline(Player player, String skill) {
        return ExperienceAPI.getLevel(player, skill);
    }

    @Override
    public boolean isChildSkill(String skill) {
        return SkillType.getSkill(skill).isChildSkill();
    }

    @Override
    public String getSkillName(String skill) {
        return SkillType.getSkill(skill).getName();
    }

    @SuppressWarnings("unchecked") // reflection
    @Override
    public List<PlayerStat> readLeaderboard(String skill, int pageNumber, int statsPerPage) {
        DatabaseManager databaseManager = mcMMO.getDatabaseManager();
        SkillType skillType = SkillType.getSkill(skill);
        try {
            Method readLeaderboard = databaseManager.getClass().getDeclaredMethod("readLeaderboard", SkillType.class, int.class, int.class);
            return (List<PlayerStat>) readLeaderboard.invoke(databaseManager, skillType, pageNumber, statsPerPage);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException && !(cause instanceof NullPointerException)) {
                throw (RuntimeException) cause;
            }
            return Collections.emptyList();
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }
}
