#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec4 aColor;
layout(location=2) in vec2 aUv;

uniform mat4 uTransform;
uniform mat4 uView;
uniform mat4 uProjection;

out vec4 fColor;
out vec2 fUv;

void main() {
    fColor = aColor;
    fUv = aUv;
    gl_Position = uProjection * uView * (uTransform * vec4(aPos, 1.0));
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fUv;

uniform sampler2D TEX_SAMPLER;

out vec4 color;

void main() {
//    color =texture(TEX_SAMPLER, fUv);
    color = fColor;
}