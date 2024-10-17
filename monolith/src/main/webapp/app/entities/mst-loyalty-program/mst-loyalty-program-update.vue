<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstLoyaltyProgram.home.createOrEditLabel"
          data-cy="MstLoyaltyProgramCreateUpdateHeading"
          v-text="t$('monolithApp.mstLoyaltyProgram.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstLoyaltyProgram.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstLoyaltyProgram.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstLoyaltyProgram.pointsEarned')"
              for="mst-loyalty-program-pointsEarned"
            ></label>
            <input
              type="number"
              class="form-control"
              name="pointsEarned"
              id="mst-loyalty-program-pointsEarned"
              data-cy="pointsEarned"
              :class="{ valid: !v$.pointsEarned.$invalid, invalid: v$.pointsEarned.$invalid }"
              v-model.number="v$.pointsEarned.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstLoyaltyProgram.membershipTier')"
              for="mst-loyalty-program-membershipTier"
            ></label>
            <input
              type="text"
              class="form-control"
              name="membershipTier"
              id="mst-loyalty-program-membershipTier"
              data-cy="membershipTier"
              :class="{ valid: !v$.membershipTier.$invalid, invalid: v$.membershipTier.$invalid }"
              v-model="v$.membershipTier.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstLoyaltyProgram.customer')"
              for="mst-loyalty-program-customer"
            ></label>
            <select
              class="form-control"
              id="mst-loyalty-program-customer"
              data-cy="customer"
              name="customer"
              v-model="mstLoyaltyProgram.customer"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstLoyaltyProgram.customer && mstCustomerOption.id === mstLoyaltyProgram.customer.id
                    ? mstLoyaltyProgram.customer
                    : mstCustomerOption
                "
                v-for="mstCustomerOption in mstCustomers"
                :key="mstCustomerOption.id"
              >
                {{ mstCustomerOption.id }}
              </option>
            </select>
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
<script lang="ts" src="./mst-loyalty-program-update.component.ts"></script>
