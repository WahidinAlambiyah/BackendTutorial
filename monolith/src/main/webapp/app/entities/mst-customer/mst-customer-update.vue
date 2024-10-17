<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstCustomer.home.createOrEditLabel"
          data-cy="MstCustomerCreateUpdateHeading"
          v-text="t$('monolithApp.mstCustomer.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstCustomer.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstCustomer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.firstName')" for="mst-customer-firstName"></label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="mst-customer-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
              required
            />
            <div v-if="v$.firstName.$anyDirty && v$.firstName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.lastName')" for="mst-customer-lastName"></label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="mst-customer-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
              required
            />
            <div v-if="v$.lastName.$anyDirty && v$.lastName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lastName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.email')" for="mst-customer-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="mst-customer-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.phoneNumber')" for="mst-customer-phoneNumber"></label>
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="mst-customer-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !v$.phoneNumber.$invalid, invalid: v$.phoneNumber.$invalid }"
              v-model="v$.phoneNumber.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.address')" for="mst-customer-address"></label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="mst-customer-address"
              data-cy="address"
              :class="{ valid: !v$.address.$invalid, invalid: v$.address.$invalid }"
              v-model="v$.address.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCustomer.loyaltyPoints')" for="mst-customer-loyaltyPoints"></label>
            <input
              type="number"
              class="form-control"
              name="loyaltyPoints"
              id="mst-customer-loyaltyPoints"
              data-cy="loyaltyPoints"
              :class="{ valid: !v$.loyaltyPoints.$invalid, invalid: v$.loyaltyPoints.$invalid }"
              v-model.number="v$.loyaltyPoints.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-customer-update.component.ts"></script>
