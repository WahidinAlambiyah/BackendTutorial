/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCityUpdate from './mst-city-update.vue';
import MstCityService from './mst-city.service';
import AlertService from '@/shared/alert/alert.service';

import MstProvinceService from '@/entities/mst-province/mst-province.service';

type MstCityUpdateComponentType = InstanceType<typeof MstCityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstCityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstCity Management Update Component', () => {
    let comp: MstCityUpdateComponentType;
    let mstCityServiceStub: SinonStubbedInstance<MstCityService>;

    beforeEach(() => {
      route = {};
      mstCityServiceStub = sinon.createStubInstance<MstCityService>(MstCityService);
      mstCityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstCityService: () => mstCityServiceStub,
          mstProvinceService: () =>
            sinon.createStubInstance<MstProvinceService>(MstProvinceService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstCityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCity = mstCitySample;
        mstCityServiceStub.update.resolves(mstCitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCityServiceStub.update.calledWith(mstCitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstCityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstCityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCity = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstCityServiceStub.find.resolves(mstCitySample);
        mstCityServiceStub.retrieve.resolves([mstCitySample]);

        // WHEN
        route = {
          params: {
            mstCityId: '' + mstCitySample.id,
          },
        };
        const wrapper = shallowMount(MstCityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstCity).toMatchObject(mstCitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCityServiceStub.find.resolves(mstCitySample);
        const wrapper = shallowMount(MstCityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
