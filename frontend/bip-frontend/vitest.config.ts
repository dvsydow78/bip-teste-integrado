// import { defineConfig } from 'vitest/config';

// export default defineConfig({
//   test: {
//     globals: true,
//     environment: 'jsdom',
//     include: ['src/**/*.spec.ts'],
//   },
// });

import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['src/test-setup.ts'],
  },
});