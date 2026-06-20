package com.simple_block_game.common.simple24Puzzle;

import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCore;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplay;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.renderer.Block24PuzzleCoreEntityRenderer;
import com.simple_block_game.common.simple24Puzzle.renderer.Block24PuzzleDisplayEntityRenderer;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;

import static com.simple_block_game.SimpleBlockGame.REGISTRY;
import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createHorizontalBlock;

public class simple24PuzzleRegistration {

    public static void init() {
        REGISTRY.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_24PUZZLE_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.CALCITE)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_24puzzle_core"));
    }

    // 24 puzzle
    public static final BlockEntry<Block24PuzzleCore> BLOCK_24PUZZLE_CORE = REGISTRY
            .block(REGISTRY, "24puzzle_core", Block24PuzzleCore::new)
            .langCn("24点核心方块")
            .lang("24 Puzzle Core")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple24puzzle/24puzzle_core")))
                    .addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.24puzzle_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.24puzzle_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.24puzzle_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.24puzzle_core.4")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.24puzzle_core.5")));
                    }))
            .register();

    public static final BlockEntry<Block24PuzzleDisplay> BLOCK_24PUZZLE_DISPLAY = REGISTRY
            .block(REGISTRY, "24puzzle_display", Block24PuzzleDisplay::new)
            .langCn("24点显示方块")
            .lang("24 Puzzle Display")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple24puzzle/24puzzle_display"))))
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_24PUZZLE_REFRESH = REGISTRY
            .block(REGISTRY, "24puzzle_refresh", p -> BaseRotatedRefreshBlock.create(p, "simple24puzzle"))
            .langCn("24点控制方块")
            .lang("24 Puzzle Refresh")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<Block24PuzzleCoreEntity> BLOCK_24PUZZLE_CORE_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "24puzzle_core_entity", (_, p, s) -> new Block24PuzzleCoreEntity(p, s))
            .validBlock(BLOCK_24PUZZLE_CORE)
            .renderer(() -> () -> Block24PuzzleCoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<Block24PuzzleDisplayEntity> BLOCK_24PUZZLE_DISPLAY_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "24puzzle_display_entity", (_, p, s) -> new Block24PuzzleDisplayEntity(p, s))
            .validBlock(BLOCK_24PUZZLE_DISPLAY)
            .renderer(() -> () -> Block24PuzzleDisplayEntityRenderer::new)
            .register();
}
