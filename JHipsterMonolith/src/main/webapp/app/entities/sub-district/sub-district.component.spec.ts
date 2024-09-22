/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import SubDistrict from './sub-district.vue';
import SubDistrictService from './sub-district.service';
import AlertService from '@/shared/alert/alert.service';

type SubDistrictComponentType = InstanceType<typeof SubDistrict>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('SubDistrict Management Component', () => {
    let subDistrictServiceStub: SinonStubbedInstance<SubDistrictService>;
    let mountOptions: MountingOptions<SubDistrictComponentType>['global'];

    beforeEach(() => {
      subDistrictServiceStub = sinon.createStubInstance<SubDistrictService>(SubDistrictService);
      subDistrictServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          subDistrictService: () => subDistrictServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        subDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(SubDistrict, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.subDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(SubDistrict, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: SubDistrictComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(SubDistrict, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        subDistrictServiceStub.retrieve.reset();
        subDistrictServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        subDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.retrieve.called).toBeTruthy();
        expect(comp.subDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(subDistrictServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        subDistrictServiceStub.retrieve.reset();
        subDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(subDistrictServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.subDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(subDistrictServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        subDistrictServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeSubDistrict();
        await comp.$nextTick(); // clear components

        // THEN
        expect(subDistrictServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(subDistrictServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
