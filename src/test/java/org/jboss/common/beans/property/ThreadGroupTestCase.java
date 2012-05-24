/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.common.beans.property;

import org.junit.BeforeClass;
import org.junit.Test;

import java.beans.PropertyEditor;
import java.util.Properties;
import java.util.concurrent.*;

import static org.junit.Assert.assertNotNull;

/**
 * This test fails on JDK 7.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ThreadGroupTestCase {
    @BeforeClass
    public static void beforeClass() {
        // initialize the property editors in the current thread group
        PropertyEditors.init();
    }

    @Test
    public void testDifferentThread() throws Exception {
        final FutureTask<PropertyEditor> task = new FutureTask<PropertyEditor>(new Callable<PropertyEditor>() {
            @Override
            public PropertyEditor call() throws Exception {
                return PropertyEditors.findEditor(Properties.class);
            }
        });
        final Thread thread = new Thread(task, "Test Thread");
        thread.start();
        final PropertyEditor editor = task.get(5, TimeUnit.MINUTES);
        assertNotNull("Property editor for " + Properties.class + " could not be found", editor);
    }

    @Test
    public void testDifferentThreadGroup() throws Exception {
        final ThreadGroup group = new ThreadGroup("Test Group");
        final FutureTask<PropertyEditor> task = new FutureTask<PropertyEditor>(new Callable<PropertyEditor>() {
            @Override
            public PropertyEditor call() throws Exception {
                return PropertyEditors.findEditor(Properties.class);
            }
        });
        final Thread thread = new Thread(group, task, "Test Thread");
        thread.start();
        final PropertyEditor editor = task.get(5, TimeUnit.MINUTES);
        assertNotNull("Property editor for " + Properties.class + " could not be found", editor);
    }

}
