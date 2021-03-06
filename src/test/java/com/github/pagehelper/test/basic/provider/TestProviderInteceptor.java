/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.pagehelper.test.basic.provider;

import com.github.pagehelper.BoundSqlInterceptor;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.mapper.ProviderMethod;
import com.github.pagehelper.mapper.UserMapper;
import com.github.pagehelper.util.MybatisInterceptorHelper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestProviderInteceptor {

    @Test
    public void testInterceptor() {
        SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        try {
            PageHelper.startPage(1, 10).boundSqlInterceptor(new BoundSqlInterceptor() {
                @Override
                public BoundSql boundSql(Type type, BoundSql boundSql, CacheKey cacheKey, Chain chain) {
                    System.out.println("before: " + boundSql.getSql());
                    BoundSql doBoundSql = chain.doBoundSql(type, boundSql, cacheKey);
                    System.out.println("after: " + doBoundSql.getSql());
                    if (type == Type.ORIGINAL) {
                        Assert.assertTrue(doBoundSql.getSql().contains(TestBoundSqlInterceptor.COMMENT));
                    }
                    return doBoundSql;
                }
            });
            String str = "???";
            userMapper.selectSimple(str);
            assertEquals(new ProviderMethod().selectSimple(str), SqlCache.get());
        } finally {
            SqlCache.remove();
            sqlSession.close();
        }
    }

}
