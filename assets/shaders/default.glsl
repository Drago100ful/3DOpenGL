#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec4 aColor;
layout(location=2) in vec2 aUv;
layout(location=3) in float aTextureId;

uniform mat4 uTransform;
uniform mat4 uView;
uniform mat4 uProjection;

out vec4 fColor;
out vec2 fUv;
out float fTextureId;

void main() {
    fColor = aColor;
    fUv = aUv;
    fTextureId = aTextureId;
    gl_Position = uProjection * uView * (uTransform * vec4(aPos, 1.0));
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fUv;
in float fTextureId;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {

    if (fTextureId > 0) {
        color = fColor * texture(uTextures[int(fTextureId)], fUv);
    } else {
        color = vec4(fUv, 0, 1);
    }

}