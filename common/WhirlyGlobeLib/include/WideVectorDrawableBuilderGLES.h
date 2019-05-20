/*
 *  WideVectorDrawableBuilderGLES.h
 *  WhirlyGlobeLib
 *
 *  Created by Steve Gifford on 5/14/19.
 *  Copyright 2011-2019 mousebird consulting
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

#import "WideVectorDrawableBuilder.h"
#import "BasicDrawableBuilderGLES.h"

namespace WhirlyKit
{

// Shader name
//#define kWideVectorShaderName "Wide Vector Shader"
//#define kWideVectorGlobeShaderName "Wide Vector Shader Globe"

/// Construct and return the wide vector shader program
ProgramGLES *BuildWideVectorProgramGLES(const std::string &name,SceneRenderer *renderer);
/// This version is for the 3D globe
ProgramGLES *BuildWideVectorGlobeProgramGLES(const std::string &name,SceneRenderer *renderer);

/// OpenGL version of the WideVectorDrawable Builder
class WideVectorDrawableBuilderGLES : public BasicDrawableBuilderGLES, public WideVectorDrawableBuilder
{
public:
    WideVectorDrawableBuilderGLES(const std::string &name);
    
    virtual int addAttribute(BDAttributeDataType dataType,StringIdentity nameID,int numThings = -1);

    virtual BasicDrawable *getDrawable();
};
    
}
