import { createRouter as createVueRouter, createWebHistory } from 'vue-router';

const Home = () => import('@/core/home/home.vue');
const Error = () => import('@/core/error/error.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory(),
    routes: [
      {
        path: '/',
        name: 'Home',
        component: Home,
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      ...account,
      ...admin,
      entities,
      ...pages,
    ],
  });

const router = createRouter();

export const lazyRoutes = Promise.all([]);

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    await lazyRoutes;
    if (router.resolve(to.fullPath).matched.length > 0) {
      next({ path: to.fullPath });
      return;
    }

    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
