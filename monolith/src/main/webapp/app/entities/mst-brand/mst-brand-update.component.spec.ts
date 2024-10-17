/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstBrandUpdate from './mst-brand-update.vue';
import MstBrandService from './mst-brand.service';
import AlertService from '@/shared/alert/alert.service';

type MstBrandUpdateComponentType = InstanceType<typeof MstBrandUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstBrandSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstBrandUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstBrand Management Update Component', () => {
    let comp: MstBrandUpdateComponentType;
    let mstBrandServiceStub: SinonStubbedInstance<MstBrandService>;

    beforeEach(() => {
      route = {};
      mstBrandServiceStub = sinon.createStubInstance<MstBrandService>(MstBrandService);
      mstBrandServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          mstBrandService: () => mstBrandServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstBrandUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstBrand = mstBrandSample;
        mstBrandServiceStub.update.resolves(mstBrandSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstBrandServiceStub.update.calledWith(mstBrandSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstBrandServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstBrandUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstBrand = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstBrandServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstBrandServiceStub.find.resolves(mstBrandSample);
        mstBrandServiceStub.retrieve.resolves([mstBrandSample]);

        // WHEN
        route = {
          params: {
            mstBrandId: '' + mstBrandSample.id,
          },
        };
        const wrapper = shallowMount(MstBrandUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstBrand).toMatchObject(mstBrandSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstBrandServiceStub.find.resolves(mstBrandSample);
        const wrapper = shallowMount(MstBrandUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
