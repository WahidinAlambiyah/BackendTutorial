/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import MstLoyaltyProgram from './mst-loyalty-program.vue';
import MstLoyaltyProgramService from './mst-loyalty-program.service';
import AlertService from '@/shared/alert/alert.service';

type MstLoyaltyProgramComponentType = InstanceType<typeof MstLoyaltyProgram>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('MstLoyaltyProgram Management Component', () => {
    let mstLoyaltyProgramServiceStub: SinonStubbedInstance<MstLoyaltyProgramService>;
    let mountOptions: MountingOptions<MstLoyaltyProgramComponentType>['global'];

    beforeEach(() => {
      mstLoyaltyProgramServiceStub = sinon.createStubInstance<MstLoyaltyProgramService>(MstLoyaltyProgramService);
      mstLoyaltyProgramServiceStub.retrieve.resolves({ headers: {} });

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
          mstLoyaltyProgramService: () => mstLoyaltyProgramServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstLoyaltyProgramServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(MstLoyaltyProgram, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.mstLoyaltyPrograms[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(MstLoyaltyProgram, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: MstLoyaltyProgramComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(MstLoyaltyProgram, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        mstLoyaltyProgramServiceStub.retrieve.reset();
        mstLoyaltyProgramServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        mstLoyaltyProgramServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.retrieve.called).toBeTruthy();
        expect(comp.mstLoyaltyPrograms[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(mstLoyaltyProgramServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        mstLoyaltyProgramServiceStub.retrieve.reset();
        mstLoyaltyProgramServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(mstLoyaltyProgramServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.mstLoyaltyPrograms[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(mstLoyaltyProgramServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        mstLoyaltyProgramServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeMstLoyaltyProgram();
        await comp.$nextTick(); // clear components

        // THEN
        expect(mstLoyaltyProgramServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(mstLoyaltyProgramServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
