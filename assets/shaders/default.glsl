#type vertex
#version 460 core
layout(location=0) in int aPos;
layout(location=1) in vec4 aColor;
layout(location=2) in vec2 aUv;
layout(location=3) in float aTextureId;

uniform mat4 uTransform;
uniform mat4 uView;
uniform mat4 uProjection;

out vec4 fColor;
out vec2 fUv;
out float fTextureId;

const int CHUNK_X = 3;
const int CHUNK_Y = 3;
const int CHUNK_Z = 3;

const float packX = 1024 / (CHUNK_X + 1);
const float packY = 1024 / (CHUNK_Y + 1);
const float packZ = 1024 / (CHUNK_Z + 1);

void main() {
    fColor = aColor;
    fUv = aUv;
    fTextureId = aTextureId;
    gl_Position = uProjection * uView * (uTransform * vec4((aPos >> 20) / packX, ((aPos >> 10) & (1023)) / packY, ((aPos) & (1023)) / packZ, 1.0));
}

#type fragment
#version 460 core

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