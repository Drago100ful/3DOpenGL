#type vertex
#version 460 core
layout(location=0) in int aPos;
layout(location=1) in int aColor;
layout(location=2) in vec2 aUv;
layout(location=3) in float aTextureId;

uniform mat4 uTransform;
uniform mat4 uView;
uniform mat4 uProjection;

flat out int fColor;
out vec2 fUv;
out float fTextureId;

const int CHUNK_X = 16;
const int CHUNK_Y = 128;
const int CHUNK_Z = 16;

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

flat in int fColor;
in vec2 fUv;
in float fTextureId;

uniform sampler2D uTextures[8];

out vec4 color;

const float r = ((fColor >> 24) & 0xFF) / 255.0;
const float g = ((fColor >> 16) & 0xFF) / 255.0;
const float b = ((fColor >> 8 )& 0xFF) / 255.0;
const float a = ((fColor) & 0xFF) / 255.0;

void main() {

    if (fTextureId > 0) {
        color = vec4(r,g,b,a) * texture(uTextures[int(fTextureId)], fUv);
    } else {
        color = vec4(r,g,b,a);
    }

}