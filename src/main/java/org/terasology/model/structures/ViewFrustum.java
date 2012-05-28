/*
 * Copyright 2011 Benjamin Glatzel <benjamin.glatzel@me.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.model.structures;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.terasology.game.CoreRegistry;
import org.terasology.rendering.world.WorldRenderer;

import javax.vecmath.Vector3d;
import java.nio.FloatBuffer;

/**
 * View frustum usable for frustum culling.
 *
 * @author Benjamin Glatzel <benjamin.glatzel@me.com>
 */
public class ViewFrustum {

    private final FrustumPlane[] _planes = new FrustumPlane[6];

    private final FloatBuffer _proj = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer _model = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer _clip = BufferUtils.createFloatBuffer(16);

    /**
     * Init. a new view frustum.
     */
    public ViewFrustum() {
        for (int i = 0; i < 6; i++)
            _planes[i] = new FrustumPlane();
    }

    /**
     * Updates the view frustum using the currently active modelview and projection matrices.
     */
    public void updateFrustum() {
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, _proj);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, _model);
        for (int ci = 0; ci < _clip.array().length; ci++)
        {
            // Do we use a quick math method for this?
            int mi = (int)Math.floor(ci / 4);
            int mci = ci % 4;
            _clip.put(ci, _model.get(mi * 4) * _proj.get(mci) +
                    _model.get(mi * 4 + 1) * _proj.get(mci + 4) +
                    _model.get(mi * 4 + 2) * _proj.get(mci + 8) +
                    _model.get(mi * 4 + 3) * _proj.get(mci + 12));
        }
        for (int plane = 0; plane < _planes.length; plane++)
        {
            int mp = (int)Math.floor(plane / 2);
            _planes[plane].setA(_clip.get(3) - _clip.get(mp));
            _planes[plane].setB(_clip.get(7) - _clip.get(mp + 4));
            _planes[plane].setC(_clip.get(11) - _clip.get(mp + 8));
            _planes[plane].setD(_clip.get(15) - _clip.get(mp + 12));
            _planes[plane].normalize();
        }
    }

    /**
     * Returns true if the given point intersects the view frustum.
     */
    public boolean intersects(double x, double y, double z) {
        for (int i = 0; i < 6; i++) {
            if (_planes[i].getA() * x + _planes[i].getB() * y + _planes[i].getC() * z + _planes[i].getD() <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if this view frustum intersects the given AABB.
     */
    public boolean intersects(AABB aabb) {

        Vector3d[] aabbVertices = aabb.getVertices();

        Vector3d cp = CoreRegistry.get(WorldRenderer.class).getActiveCamera().getPosition();

        for (int i = 0; i < 6; i++) {
            if (_planes[i].getA() * (aabbVertices[0].x - cp.x) + _planes[i].getB() * (aabbVertices[0].y - cp.y) + _planes[i].getC() * (aabbVertices[0].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[1].x - cp.x) + _planes[i].getB() * (aabbVertices[1].y - cp.y) + _planes[i].getC() * (aabbVertices[1].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[2].x - cp.x) + _planes[i].getB() * (aabbVertices[2].y - cp.y) + _planes[i].getC() * (aabbVertices[2].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[3].x - cp.x) + _planes[i].getB() * (aabbVertices[3].y - cp.y) + _planes[i].getC() * (aabbVertices[3].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[4].x - cp.x) + _planes[i].getB() * (aabbVertices[4].y - cp.y) + _planes[i].getC() * (aabbVertices[4].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[5].x - cp.x) + _planes[i].getB() * (aabbVertices[5].y - cp.y) + _planes[i].getC() * (aabbVertices[5].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[6].x - cp.x) + _planes[i].getB() * (aabbVertices[6].y - cp.y) + _planes[i].getC() * (aabbVertices[6].z - cp.z) + _planes[i].getD() > 0)
                continue;
            if (_planes[i].getA() * (aabbVertices[7].x - cp.x) + _planes[i].getB() * (aabbVertices[7].y - cp.y) + _planes[i].getC() * (aabbVertices[7].z - cp.z) + _planes[i].getD() > 0)
                continue;
            return false;
        }

        return true;
    }
}
