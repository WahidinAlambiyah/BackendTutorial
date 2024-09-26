/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstRegionUpdate from './mst-region-update.vue';
import MstRegionService from './mst-region.service';
import AlertService from '@/shared/alert/alert.service';

type MstRegionUpdateComponentType = InstanceType<typeof MstRegionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstRegionSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstRegionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstRegion Management Update Component', () => {
    let comp: MstRegionUpdateComponentType;
    let mstRegionServiceStub: SinonStubbedInstance<MstRegionService>;

    beforeEach(() => {
      route = {};
      mstRegionServiceStub = sinon.createStubInstance<MstRegionService>(MstRegionService);
      mstRegionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstRegionService: () => mstRegionServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstRegionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstRegion = mstRegionSample;
        mstRegionServiceStub.update.resolves(mstRegionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstRegionServiceStub.update.calledWith(mstRegionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstRegionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstRegionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstRegion = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstRegionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstRegionServiceStub.find.resolves(mstRegionSample);
        mstRegionServiceStub.retrieve.resolves([mstRegionSample]);

        // WHEN
        route = {
          params: {
            mstRegionId: '' + mstRegionSample.id,
          },
        };
        const wrapper = shallowMount(MstRegionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstRegion).toMatchObject(mstRegionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstRegionServiceStub.find.resolves(mstRegionSample);
        const wrapper = shallowMount(MstRegionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
