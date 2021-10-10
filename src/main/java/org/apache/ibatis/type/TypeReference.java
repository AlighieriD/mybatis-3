/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * @param <T> the referenced type
 * @since 3.1.0
 * @author Simone Tripodi
 */
public abstract class TypeReference<T> {

  private final Type rawType;

  /**
   * yuhao: protected 只有同包 或者 子类 可以 实例化TypeReference类
   */
  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  Type getSuperclassTypeParameter(Class<?> clazz) {
    // yuhao: 返回clazz的直接超类的Type
    Type genericSuperclass = clazz.getGenericSuperclass();
    // yuhao: 如果当前子类的直接超类是Class类型（而不是参数化类型ParameterizedType）
    if (genericSuperclass instanceof Class) {
      // yuhao: 递归向上查找
      // try to climb up the hierarchy until meet something useful
      if (TypeReference.class != genericSuperclass) {
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }

      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }

    // yuhao: 得到实际的参数类型T
    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    // yuhao: 如果rawType仍然是类型化参数 ， 返回定义类型（例如rawType为List<Integer> , 则getRawType返回List）
    if (rawType instanceof ParameterizedType) {
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
