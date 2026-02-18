/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakePackageTest {

    @Test
    void testConstructor() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg);
    }

    @Test
    void testGetConfigMode() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getConfigMode());
        assertTrue(pkg.getConfigMode().isEmpty());
    }

    @Test
    void testSetConfigMode() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setConfigMode(true);
        assertTrue(pkg.getConfigMode().isPresent());
        assertEquals(Boolean.TRUE, pkg.getConfigMode().get());
    }

    @Test
    void testGetTargetPrefix() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getTargetPrefix());
        assertTrue(pkg.getTargetPrefix().isEmpty());
    }

    @Test
    void testSetTargetPrefix() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setTargetPrefix("prefix");
        assertTrue(pkg.getTargetPrefix().isPresent());
        assertEquals("prefix", pkg.getTargetPrefix().get());
    }

    @Test
    void testGetComponents() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        Collection<String> components = pkg.getComponents();
        assertNotNull(components);
        assertEquals(0, components.size());
    }

    @Test
    void testSetComponents() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setComponents(Arrays.asList("component1", "component2"));
        assertEquals(2, pkg.getComponents().size());
    }

    @Test
    void testGetProperties() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getProperties());
        assertEquals(0, pkg.getProperties().size());
    }

    @Test
    void testSetProperties() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        Map<CharSequence, CharSequence> props = new HashMap<>();
        props.put("key1", "value1");
        props.put("key2", "value2");
        pkg.setProperties(props);
        assertEquals(2, pkg.getProperties().size());
    }

    @Test
    void testGetInterfaces() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getInterfaces());
        assertEquals(0, pkg.getInterfaces().size());
    }

    @Test
    void testSetInterfaces() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setInterfaces(Arrays.asList("/path/to/interface1", "/path/to/interface2"));
        assertEquals(2, pkg.getInterfaces().size());
    }

    @Test
    void testGetStaticLibraries() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getStaticLibraries());
        assertEquals(0, pkg.getStaticLibraries().size());
    }

    @Test
    void testSetStaticLibraries() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setStaticLibraries(Arrays.asList("/path/to/lib1", "/path/to/lib2"));
        assertEquals(2, pkg.getStaticLibraries().size());
    }

    @Test
    void testGetSharedLibraries() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        assertNotNull(pkg.getSharedLibraries());
        assertEquals(0, pkg.getSharedLibraries().size());
    }

    @Test
    void testSetSharedLibraries() {
        CMakePackage pkg = new CMakePackage() {
            @Override
            public String getName() {
                return "test";
            }
        };
        pkg.setSharedLibraries(Arrays.asList("/path/to/lib1", "/path/to/lib2"));
        assertEquals(2, pkg.getSharedLibraries().size());
    }
}
