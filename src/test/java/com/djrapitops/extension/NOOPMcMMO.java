/*
 * Copyright(c) 2020 Risto Lahtela (AuroraLS3)
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

import com.gmail.nossr50.datatypes.database.PlayerStat;
import org.bukkit.entity.Player;

import java.util.List;

public class NOOPMcMMO implements McMMO {
    @Override
    public int getLevelOnline(Player player, String skill) {
        return 0;
    }

    @Override
    public boolean isChildSkill(String skill) {
        return false;
    }

    @Override
    public String getSkillName(String skill) {
        return null;
    }

    @Override
    public List<PlayerStat> readLeaderboard(String skill, int pageNumber, int statsPerPage) {
        return null;
    }
}
