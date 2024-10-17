/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDriverUpdate from './mst-driver-update.vue';
import MstDriverService from './mst-driver.service';
import AlertService from '@/shared/alert/alert.service';

type MstDriverUpdateComponentType = InstanceType<typeof MstDriverUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDriverSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstDriverUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstDriver Management Update Component', () => {
    let comp: MstDriverUpdateComponentType;
    let mstDriverServiceStub: SinonStubbedInstance<MstDriverService>;

    beforeEach(() => {
      route = {};
      mstDriverServiceStub = sinon.createStubInstance<MstDriverService>(MstDriverService);
      mstDriverServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstDriverService: () => mstDriverServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstDriverUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDriver = mstDriverSample;
        mstDriverServiceStub.update.resolves(mstDriverSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDriverServiceStub.update.calledWith(mstDriverSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstDriverServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstDriverUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDriver = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDriverServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstDriverServiceStub.find.resolves(mstDriverSample);
        mstDriverServiceStub.retrieve.resolves([mstDriverSample]);

        // WHEN
        route = {
          params: {
            mstDriverId: '' + mstDriverSample.id,
          },
        };
        const wrapper = shallowMount(MstDriverUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstDriver).toMatchObject(mstDriverSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDriverServiceStub.find.resolves(mstDriverSample);
        const wrapper = shallowMount(MstDriverUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
