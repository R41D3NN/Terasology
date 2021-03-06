package org.terasology.componentSystem.rendering;

import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;
import org.terasology.componentSystem.RenderSystem;
import org.terasology.components.BlockComponent;
import org.terasology.components.HealthComponent;
import org.terasology.entitySystem.EntityManager;
import org.terasology.entitySystem.EntityRef;
import org.terasology.game.CoreRegistry;
import org.terasology.logic.manager.AssetManager;
import org.terasology.logic.manager.ShaderManager;
import org.terasology.logic.world.IWorldProvider;
import org.terasology.rendering.assets.Texture;
import org.terasology.rendering.primitives.Mesh;
import org.terasology.rendering.primitives.Tessellator;
import org.terasology.rendering.primitives.TessellatorHelper;
import org.terasology.rendering.world.WorldRenderer;

/**
 * @author Immortius <immortius@gmail.com>
 */
public class BlockDamageRenderer implements RenderSystem {

    private EntityManager entityManager;
    private IWorldProvider worldProvider;
    private Mesh overlayMesh;
    private Texture effectsTexture;

    @Override
    public void initialise() {
        entityManager = CoreRegistry.get(EntityManager.class);
        worldProvider = CoreRegistry.get(IWorldProvider.class);
        effectsTexture = AssetManager.loadTexture("engine:effects");
        Vector2f texPos = new Vector2f(0.0f, 0.0f);
        Vector2f texWidth = new Vector2f(0.0624f, 0.0624f);

        Tessellator tessellator = new Tessellator();
        TessellatorHelper.addBlockMesh(tessellator, new Vector4f(1, 1, 1, 1), texPos, texWidth, 1.001f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        overlayMesh = tessellator.generateMesh();
    }

    @Override
    public void renderOverlay() {
        if (effectsTexture == null) return;

        ShaderManager.getInstance().enableDefaultTextured();
        glBindTexture(GL11.GL_TEXTURE_2D, effectsTexture.getId());
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL_DST_COLOR, GL_ZERO);
        Vector3d cameraPosition = CoreRegistry.get(WorldRenderer.class).getActiveCamera().getPosition();

        for (EntityRef entity : entityManager.iteratorEntities(HealthComponent.class, BlockComponent.class)) {
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health.currentHealth == health.maxHealth) continue;

            BlockComponent blockComp = entity.getComponent(BlockComponent.class);
            if (!worldProvider.isChunkAvailableAt(blockComp.getPosition())) continue;

            glPushMatrix();
            glTranslated(blockComp.getPosition().x - cameraPosition.x, blockComp.getPosition().y - cameraPosition.y, blockComp.getPosition().z - cameraPosition.z);

            float offset = java.lang.Math.round((1.0f - (float) health.currentHealth / health.maxHealth) * 10.0f) * 0.0625f;

            glMatrixMode(GL_TEXTURE);
            glPushMatrix();
            glTranslatef(offset, 0f, 0f);
            glMatrixMode(GL_MODELVIEW);

            overlayMesh.render();

            glPopMatrix();

            glMatrixMode(GL_TEXTURE);
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);
        }
        glDisable(GL11.GL_BLEND);
    }

    @Override
    public void renderFirstPerson() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderOpaque() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderTransparent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
