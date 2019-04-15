/*
    Copyright(c) 2019 Risto Lahtela (Rsl1122)

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DataExtension for McMMO.
 *
 * @author Rsl1122
 */
@PluginInfo(name = "mcMMO", iconName = "compass", iconFamily = Family.REGULAR, color = Color.INDIGO)
public class McMMOExtension implements DataExtension {

    public McMMOExtension() {
    }

    private PlayerProfile getProfile(UUID playerUUID) {
        return mcMMO.getDatabaseManager().loadPlayerProfile(playerUUID);
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE
        };
    }

    @TableProvider(tableColor = Color.INDIGO)
    public Table skillTable(UUID playerUUID) {
        Table.Factory table = Table.builder()
                .columnOne("Skill", Icon.called("star").build())
                .columnTwo("Level", Icon.called("plus").build());

        PlayerProfile profile = getProfile(playerUUID);

        List<PrimarySkillType> skills = Arrays.stream(PrimarySkillType.values()).distinct().collect(Collectors.toList());
        for (PrimarySkillType skill : skills) {
            table.addRow(formatSkillName(skill.getName()), profile.getSkillLevel(skill));
        }

        return table.build();
    }

    private String formatSkillName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    @TableProvider(tableColor = Color.INDIGO)
    public Table leaderboard() {
        Table.Factory table = Table.builder()
                .columnOne("Skill", Icon.called("star").build())
                .columnTwo("#1", Icon.called("user").build())
                .columnThree("#2", Icon.called("user").build())
                .columnFour("#3", Icon.called("user").build());

        List<PrimarySkillType> skills = Arrays.stream(PrimarySkillType.values()).distinct().collect(Collectors.toList());

        for (PrimarySkillType skill : skills) {
            List<PlayerStat> skillLeaders = mcMMO.getDatabaseManager().readLeaderboard(skill, 1, 3);

            table.addRow(
                    formatSkillName(skill.getName()),
                    getPlayerName(skillLeaders, 0),
                    getPlayerName(skillLeaders, 1),
                    getPlayerName(skillLeaders, 2)
            );
        }

        return table.build();
    }

    private Optional<String> getPlayerName(List<PlayerStat> from, int index) {
        if (index < from.size()) {
            return Optional.ofNullable(from.get(index)).map(stat -> stat.name);
        }
        return Optional.empty();
    }
}