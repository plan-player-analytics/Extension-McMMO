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

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.InvalidateMethod;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.StringProvider;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import com.gmail.nossr50.api.exceptions.InvalidPlayerException;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DataExtension for McMMO.
 *
 * @author Vankka
 * @author AuroraLS3
 */
@PluginInfo(name = "mcMMO", iconName = "compass", iconFamily = Family.REGULAR, color = Color.INDIGO)
@InvalidateMethod("leaderboard")
public class McMMOExtension implements DataExtension {

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.SERVER_EXTENSION_REGISTER,
                CallEvents.SERVER_PERIODICAL
        };
    }

    private final McMMO mcMMO;

    public McMMOExtension(McMMO mcMMO) {
        this.mcMMO = mcMMO;
    }

    private int getLevel(UUID playerUUID, String skillType) {
        Player player = Bukkit.getPlayer(playerUUID);
        try {
            if (player != null) return mcMMO.getLevelOnline(player, skillType);
        } catch (McMMOPlayerNotFoundException | NullPointerException | IndexOutOfBoundsException ignored) {
            // this doesn't mean there is no player data, it might just not be loaded yet
            // so we try getting the 'offline' data
        }

        try {
            return ExperienceAPI.getLevelOffline(playerUUID, skillType);
        } catch (InvalidPlayerException ignored) {
            return 0;
        }
    }

    private String getXP(UUID playerUUID, String skillType) {
        if (mcMMO.isChildSkill(skillType)) return "Child skill";

        Player player = Bukkit.getPlayer(playerUUID);
        try {
            if (player != null) {
                int xpToNextLevel = ExperienceAPI.getXPToNextLevel(player, skillType);
                if (xpToNextLevel <= 0) return "Max level";
                return ExperienceAPI.getXP(player, skillType) + "/" + xpToNextLevel;
            }
        } catch (McMMOPlayerNotFoundException | IndexOutOfBoundsException ignored) {
            // this doesn't mean there is no player data, it might just not be loaded yet
            //  so we try getting the 'offline' data
        }

        try {
            int xpToNextLevel = ExperienceAPI.getOfflineXPToNextLevel(playerUUID, skillType);
            if (xpToNextLevel <= 0) return "Max level";
            return ExperienceAPI.getOfflineXP(playerUUID, skillType) + "/" + xpToNextLevel;
        } catch (InvalidPlayerException | IndexOutOfBoundsException ignored) {
            return "Unknown";
        }
    }

    private Optional<String> getPlayerName(List<PlayerStat> from, int index) {
        if (index < from.size()) {
            return Optional.ofNullable(from.get(index)).map(stat -> stat.name);
        }
        return Optional.empty();
    }

    // Player data

    @TableProvider(
            tableColor = Color.INDIGO
    )
    public Table levels(UUID playerUUID) {
        Table.Factory table = Table.builder()
                .columnOne("Skill name", Icon.called("magic").of(Family.SOLID).build())
                .columnTwo("Level", Icon.called("chart-bar").of(Family.SOLID).build())
                .columnThree("XP", Icon.called("tasks").of(Family.SOLID).build());

        for (String skill : SkillAPI.getSkills()) {
            int level = getLevel(playerUUID, skill);
            String xp = getXP(playerUUID, skill);
            table.addRow(mcMMO.getSkillName(skill), level, xp);
        }

        return table.build();
    }

    @StringProvider(
            text = "Highest Level Skill",
            description = "The highest mcmmo skill level",
            iconName = "magic",
            iconFamily = Family.SOLID,
            iconColor = Color.GREEN,
            showInPlayerTable = true
    )
    public String highestSkill(UUID playerUUID) {
        String highestSkill = "None";
        int maxLevel = 0;

        for (String skill : SkillAPI.getSkills()) {
            int skillLevel = getLevel(playerUUID, skill);
            if (maxLevel < skillLevel) {
                maxLevel = skillLevel;
                highestSkill = mcMMO.getSkillName(skill);
            }
        }
        return highestSkill + " (" + maxLevel + ")";
    }

    // Server data

    //  DISABLED  @TableProvider(tableColor = Color.INDIGO)
    public Table leaderboard() {
        Table.Factory table = Table.builder()
                .columnOne("Skill", Icon.called("star").build())
                .columnTwo("#1", Icon.called("user").build())
                .columnThree("#2", Icon.called("user").build())
                .columnFour("#3", Icon.called("user").build());

        for (String skill : SkillAPI.getSkills()) {
            List<PlayerStat> skillLeaders = mcMMO.readLeaderboard(skill, 1, 3);

            if (skillLeaders.isEmpty()) continue;

            table.addRow(
                    mcMMO.getSkillName(skill),
                    getPlayerName(skillLeaders, 0).orElse("-"),
                    getPlayerName(skillLeaders, 1).orElse("-"),
                    getPlayerName(skillLeaders, 2).orElse("-")
            );
        }

        return table.build();
    }
}
