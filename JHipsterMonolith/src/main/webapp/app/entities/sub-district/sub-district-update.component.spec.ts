/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SubDistrictUpdate from './sub-district-update.vue';
import SubDistrictService from './sub-district.service';
import AlertService from '@/shared/alert/alert.service';

import DistrictService from '@/entities/district/district.service';

type SubDistrictUpdateComponentType = InstanceType<typeof SubDistrictUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const subDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<SubDistrictUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('SubDistrict Management Update Component', () => {
    let comp: SubDistrictUpdateComponentType;
    let subDistrictServiceStub: SinonStubbedInstance<SubDistrictService>;

    beforeEach(() => {
      route = {};
      subDistrictServiceStub = sinon.createStubInstance<SubDistrictService>(SubDistrictService);
      subDistrictServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          subDistrictService: () => subDistrictServiceStub,
          districtService: () =>
            sinon.createStubInstance<DistrictService>(DistrictService, {
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
        const wrapper = shallowMount(SubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.subDistrict = subDistrictSample;
        subDistrictServiceStub.update.resolves(subDistrictSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.update.calledWith(subDistrictSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        subDistrictServiceStub.create.resolves(entity);
        const wrapper = shallowMount(SubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.subDistrict = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        subDistrictServiceStub.find.resolves(subDistrictSample);
        subDistrictServiceStub.retrieve.resolves([subDistrictSample]);

        // WHEN
        route = {
          params: {
            subDistrictId: '' + subDistrictSample.id,
          },
        };
        const wrapper = shallowMount(SubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.subDistrict).toMatchObject(subDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        subDistrictServiceStub.find.resolves(subDistrictSample);
        const wrapper = shallowMount(SubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
