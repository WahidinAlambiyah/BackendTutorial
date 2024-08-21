/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import DistrictUpdate from './district-update.vue';
import DistrictService from './district.service';
import AlertService from '@/shared/alert/alert.service';

import CityService from '@/entities/city/city.service';

type DistrictUpdateComponentType = InstanceType<typeof DistrictUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const districtSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<DistrictUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('District Management Update Component', () => {
    let comp: DistrictUpdateComponentType;
    let districtServiceStub: SinonStubbedInstance<DistrictService>;

    beforeEach(() => {
      route = {};
      districtServiceStub = sinon.createStubInstance<DistrictService>(DistrictService);
      districtServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          districtService: () => districtServiceStub,
          cityService: () =>
            sinon.createStubInstance<CityService>(CityService, {
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
        const wrapper = shallowMount(DistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.district = districtSample;
        districtServiceStub.update.resolves(districtSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(districtServiceStub.update.calledWith(districtSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        districtServiceStub.create.resolves(entity);
        const wrapper = shallowMount(DistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.district = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(districtServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        districtServiceStub.find.resolves(districtSample);
        districtServiceStub.retrieve.resolves([districtSample]);

        // WHEN
        route = {
          params: {
            districtId: '' + districtSample.id,
          },
        };
        const wrapper = shallowMount(DistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.district).toMatchObject(districtSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        districtServiceStub.find.resolves(districtSample);
        const wrapper = shallowMount(DistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
