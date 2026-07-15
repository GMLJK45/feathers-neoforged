package com.elenai.feathers.client.gui;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.client.ClientFeathersData;
import com.elenai.feathers.config.FeathersClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.client.gui.RowCountRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class FeathersHudOverlay {

    public final static ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "textures/gui/icons.png");
    public final static int NONE = 16;
    public final static int FULL = 34;
    public final static int FULL_FLOW = 70;
    public final static int HALF = 25;
    public final static int HALF_FLOW = 61;
    public final static int ARMORED = 52;
    public final static int HALF_ARMORED = 43;

    // Base vertical offset from the bottom of the screen, roughly matching where vanilla's
    // hunger/armor row sits above the hotbar. Under the old IGuiOverlay system this adjustment
    // was applied for you before the overlay's y coordinate was handed in; LayeredDraw.Layer
    // gives raw guiHeight instead, so we have to bake it in ourselves now.
    private static final int BASE_Y_OFFSET = -49;

    public static int k = 0;
    static float alpha = 1.0f;

    /**
     * Renders the Feathers to the hotbar
     *
     * NOTE: as of NeoForge 1.21.1's switch to vanilla's LayeredDraw system, IGuiOverlay/ForgeGui are
     * gone. That means:
     *  - screenWidth/screenHeight are now read from GuiGraphics instead of being passed in.
     *  - There is no more "gui" (ForgeGui) parameter, so gui.shouldDrawSurvivalElements() and
     *    gui.rightHeight had to be reimplemented/dropped below - see the inline notes.
     */
    public static final LayeredDraw.Layer FEATHERS = (guiGraphics, deltaTracker) -> {

        if (ClientFeathersData.getMaxFeathers() <= 0 & ClientFeathersData.getEnduranceFeathers() == 0) return;

        int fadeCooldown = FeathersClientConfig.FADE_COOLDOWN.get();
        int fadeIn = FeathersClientConfig.FADE_IN_COOLDOWN.get();
        int fadeOut = FeathersClientConfig.FADE_OUT_COOLDOWN.get();
        int xOffset = FeathersClientConfig.X_OFFSET.get();
        int yOffset = BASE_Y_OFFSET + FeathersClientConfig.Y_OFFSET.get();
        Minecraft minecraft = Minecraft.getInstance();

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int x = screenWidth / 2;
        int y = screenHeight;

        // ForgeGui#rightHeight (used to stack this overlay above potion icons etc.) no longer
        // exists - vanilla's Gui doesn't expose an equivalent, so this is a fixed config-driven
        // nudge rather than a true "detect what's occupying that space" shift. It must be computed
        // BEFORE any rendering below, since every blit call in this layer uses it.
        int rightOffset = FeathersClientConfig.AFFECTED_BY_RIGHT_HEIGHT.get() ? 10 : 0;

        boolean shouldDrawSurvivalElements = minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer()
                && minecraft.getCameraEntity() instanceof Player;

        if (!minecraft.options.hideGui && shouldDrawSurvivalElements) {
            /*
             * If enabled, decrease the overlay's alpha value relative to the fade in/out duration
             */
            if (FeathersClientConfig.FADE_WHEN_FULL.get()) {
                if (ClientFeathersData.getFeathers() == ClientFeathersData.getMaxFeathers()) {
                    if (ClientFeathersData.getFadeCooldown() == fadeCooldown && alpha > 0) {
                        alpha = alpha <= 0.025 ? 0 : alpha - 1.0f / fadeOut;
                    }
                } else {
                    alpha = alpha >= 1.0f ? 1.0f : alpha + 1.0f / fadeIn;
                }
            } else {
                alpha = 1.0f;
            }
            // Bail out before touching any RenderSystem state - previously this early-returned
            // *after* enableBlend/setShaderColor/setShaderTexture had already run, which left
            // blending enabled and the shader color stuck at alpha 0 for every other GUI element
            // rendered afterwards (e.g. the player model in the inventory screen), since they
            // share the same position-tex-color shader.
            if (alpha <= 0) return;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            RenderSystem.setShaderTexture(0, ICONS);

            /*
             * Always render the background up to the maximum feather amount
             */
            for (int i = 0; i < 10; i++) {
                if ((i + 1 <= Math.ceil((double) ClientFeathersData.getMaxFeathers() / 2.0d))) {
                    int cold = ((ClientFeathersData.isCold()) ? 18 : 0);
                    int height = (k > i * 10 && k < (i + 1) * 10) ? 2 : 0;
                    guiGraphics.blit(ICONS, x + 81 - (i * 8) + xOffset, y - rightOffset - height + yOffset, NONE,
                            cold, 9, 9, 256, 256);
                }
            }

            /*
             * Only render the currently active feathers
             */
            for (int i = 0; i < 10; i++) {
                if ((i + 1 <= Math.ceil((double) ClientFeathersData.getFeathers() / 2.0d))
                        && ClientFeathersData.getFeathers() > 0) {
                    // Check if feather is half or full
                    int type = ((i + 1 == Math.ceil((double) ClientFeathersData.getFeathers() / 2.0d)
                            && (ClientFeathersData.getFeathers() % 2 != 0)) ? HALF : FULL);

                    int height = (k > i * 10 && k < (i + 1) * 10) ? 2 : 0;

                    int cold = ((ClientFeathersData.isCold()) ? 18 : 0);
                    guiGraphics.blit(ICONS, x + 81 - (i * 8) + xOffset, y- rightOffset - height + yOffset,
                            type, cold, 9, 9, 256, 256);
                } else {
                    break;
                }
            }

            /*
             * Only render the currently worn armor
             */
            for (int i = 0; i < 10; i++) {
                if ((i + 1 <= Math.ceil((double) ClientFeathersData.getWeight() / 2.0d))
                        && (i + 1 <= Math.ceil((double) ClientFeathersData.getFeathers() / 2.0d))) {
                    int height = (k > i * 10 && k < (i + 1) * 10) ? 2 : 0;

                    // Check if feather is half or full
                    int type = ((i + 1 == Math.ceil((double) ClientFeathersData.getWeight() / 2.0d)
                            && (ClientFeathersData.getWeight() % 2 != 0)) ? HALF_ARMORED : ARMORED);

                    int lowerFeathers = (i >= Math.floor((double) ClientFeathersData.getFeathers() / 2.0d)) ? 9 : 0;

                    guiGraphics.blit(ICONS, x + 81 - (i * 8) + xOffset, y - rightOffset - height + yOffset,
                            type, lowerFeathers, 9, 9, 256, 256);
                } else {
                    break;
                }
            }

            /*
             * Render feathers past 20 in a different color
             */
            if (ClientFeathersData.isOverflowing()) {
                for (int i = 0; i < 10; i++) {
                    if (i + 1 <= Math.ceil((double) (ClientFeathersData.getFeathers() - 20) / 2.0d)) {
                        // Check if feather is half or full
                        int type = (i + 1 == Math.ceil((double) (ClientFeathersData.getFeathers() - 20) / 2.0d)
                                && ClientFeathersData.getFeathers() % 2 != 0 ? HALF_FLOW : FULL_FLOW);
                        int height = (k > i * 10 && k < (i + 1) * 10) ? 2 : 0;
                        int cold = ((ClientFeathersData.isCold()) ? 18 : 0);
                        guiGraphics.blit(ICONS, x + 81 - (i * 8) + xOffset, y- rightOffset - height + yOffset,
                                type, cold, 9, 9, 256, 256);
                    } else {
                        break;
                    }
                }
            }

            /*
             * Render the Regeneration effect
             */
            if (ClientFeathersData.getFeathers() < ClientFeathersData.getMaxFeathers()) {
                for (int i = 0; i < 10; i++) {
                    if (ClientFeathersData.getAnimationCooldown() >= 18
                            || ClientFeathersData.getAnimationCooldown() == 10) {
                        if ((i + 1 <= Math.ceil((double) ClientFeathersData.getMaxFeathers() / 2.0d))) {
                            int height = (k > i * 10 && k < (i + 1) * 10) ? 2 : 0;
                            guiGraphics.blit(ICONS, x + 81 - (i * 8) + xOffset, y - rightOffset - height + yOffset,
                                    NONE, 9, 9, 9, 256, 256);
                        }
                    }
                }
            }

            boolean stillRegenerating = ClientFeathersData.getFeathers() < ClientFeathersData.getMaxFeathers();

            if (ClientFeathersData.isEnergized() && stillRegenerating) {
                if (k == 100) {
                    k = -40;
                } else {
                    k += 2;
                }
            } else if (k != 0) {
                k = 0;
            }

            /*
             * Only render the currently active endurance feathers by line
             */
            for (int i = 0; i < Math.ceil((double) ClientFeathersData.getEnduranceFeathers() / 20.0d); i++) { //TODO: fix half feathers
                for (int j = 0; j < 10; j++) {
                    if ((((i) * 10.0d) + (j + 1) <= Math
                            .ceil((double) ClientFeathersData.getEnduranceFeathers() / 2.0d))
                            && ClientFeathersData.getEnduranceFeathers() > 0) {

                        // Check if feather is half or full
                        int type = (((j + 1) + (10 * i) == Math.ceil((double) ClientFeathersData.getEnduranceFeathers() / 2.0d)
                                && (ClientFeathersData.getEnduranceFeathers() % 2 != 0)) ? HALF : FULL);

                        guiGraphics.blit(ICONS, x + 81 - (j * 8) + xOffset,
                                y /*- 58*/ - rightOffset + yOffset - ((i) * 10), type, 9, 9, 9, 256, 256);
                    } else {
                        break;
                    }
                }
            }

            if (Feathers.OB_LOADED) {
                RowCountRenderer.drawBarRowCount(guiGraphics, x + 100 + xOffset, y - rightOffset + 10 + yOffset, ClientFeathersData.getFeathers(), true, minecraft.font);
            }

        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

    };

}