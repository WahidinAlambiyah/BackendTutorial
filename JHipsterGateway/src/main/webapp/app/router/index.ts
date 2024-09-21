import { type RouteRecordRaw, createRouter as createVueRouter, createWebHistory } from 'vue-router';
import { importRemote } from '@module-federation/utilities';

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

export const lazyRoutes = Promise.all([
  importRemote<any>({
    url: `./services/client`,
    scope: 'client',
    module: './entities-router',
  })
    .then(clientRouter => {
      router.addRoute(clientRouter.default as RouteRecordRaw);
      return clientRouter.default;
    })
    .catch(error => {
      console.log(`Error loading client menus. Make sure it's up. ${error}`);
    }),
  importRemote<any>({
    url: `./services/admin`,
    scope: 'admin',
    module: './entities-router',
  })
    .then(adminRouter => {
      router.addRoute(adminRouter.default as RouteRecordRaw);
      return adminRouter.default;
    })
    .catch(error => {
      console.log(`Error loading admin menus. Make sure it's up. ${error}`);
    }),
  importRemote<any>({
    url: `./services/landing`,
    scope: 'landing',
    module: './entities-router',
  })
    .then(landingRouter => {
      router.addRoute(landingRouter.default as RouteRecordRaw);
      return landingRouter.default;
    })
    .catch(error => {
      console.log(`Error loading landing menus. Make sure it's up. ${error}`);
    }),
]);

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
