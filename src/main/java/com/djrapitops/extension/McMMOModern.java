/*
 * Copyright(c) 2020 Risto Lahtela (Rsl1122)
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

package com.djrapitops.extension;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;

import java.util.List;

public class McMMOModern implements McMMO {

    @Override
    public int getLevelOnline(Player player, String skill) {
        return ExperienceAPI.getLevel(player, PrimarySkillType.getSkill(skill));
    }

    @Override
    public boolean isChildSkill(String skill) {
        return PrimarySkillType.getSkill(skill).isChildSkill();
    }

    @Override
    public String getSkillName(String skill) {
        return PrimarySkillType.getSkill(skill).getName();
    }

    @Override
    public List<PlayerStat> readLeaderboard(String skill, int pageNumber, int statsPerPage) {
        return mcMMO.getDatabaseManager().readLeaderboard(PrimarySkillType.getSkill(skill), pageNumber, statsPerPage);
    }
}
